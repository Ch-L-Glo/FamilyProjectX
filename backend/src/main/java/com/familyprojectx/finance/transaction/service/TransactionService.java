package com.familyprojectx.finance.transaction.service;

import com.familyprojectx.finance.category.entity.Category;
import com.familyprojectx.finance.category.repository.CategoryRepository;
import com.familyprojectx.finance.common.exception.ApiException;
import com.familyprojectx.finance.family.entity.Family;
import com.familyprojectx.finance.family.repository.FamilyRepository;
import com.familyprojectx.finance.family.service.FamilyAccessService;
import com.familyprojectx.finance.settlement.entity.Settlement;
import com.familyprojectx.finance.settlement.repository.SettlementRepository;
import com.familyprojectx.finance.split.entity.SplitType;
import com.familyprojectx.finance.split.entity.TransactionSplit;
import com.familyprojectx.finance.split.repository.TransactionSplitRepository;
import com.familyprojectx.finance.transaction.dto.CreateTransactionRequest;
import com.familyprojectx.finance.transaction.dto.SplitRequest;
import com.familyprojectx.finance.transaction.dto.TransactionResponse;
import com.familyprojectx.finance.transaction.entity.FinancialTransaction;
import com.familyprojectx.finance.transaction.entity.TransactionOwnershipType;
import com.familyprojectx.finance.transaction.repository.FinancialTransactionRepository;
import com.familyprojectx.finance.user.entity.UserAccount;
import com.familyprojectx.finance.user.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final FinancialTransactionRepository transactionRepository;
    private final TransactionSplitRepository splitRepository;
    private final SettlementRepository settlementRepository;
    private final FamilyRepository familyRepository;
    private final UserAccountRepository userAccountRepository;
    private final CategoryRepository categoryRepository;
    private final FamilyAccessService familyAccessService;

    public TransactionService(
            FinancialTransactionRepository transactionRepository,
            TransactionSplitRepository splitRepository,
            SettlementRepository settlementRepository,
            FamilyRepository familyRepository,
            UserAccountRepository userAccountRepository,
            CategoryRepository categoryRepository,
            FamilyAccessService familyAccessService
    ) {
        this.transactionRepository = transactionRepository;
        this.splitRepository = splitRepository;
        this.settlementRepository = settlementRepository;
        this.familyRepository = familyRepository;
        this.userAccountRepository = userAccountRepository;
        this.categoryRepository = categoryRepository;
        this.familyAccessService = familyAccessService;
    }

    @Transactional
    public TransactionResponse create(UUID familyId, UUID actorUserId, CreateTransactionRequest request) {
        familyAccessService.requireMember(familyId, actorUserId);
        familyAccessService.requireMember(familyId, request.paidByUserId());
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family not found"));
        UserAccount actor = user(actorUserId);
        UserAccount paidBy = user(request.paidByUserId());
        Category category = request.categoryId() == null ? null : categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Category not found in family"));
        BigDecimal amountBase = request.amountOriginal().multiply(request.exchangeRate()).setScale(4, RoundingMode.HALF_UP);

        FinancialTransaction transaction = transactionRepository.save(new FinancialTransaction(
                family,
                request.amountOriginal(),
                request.currencyOriginal(),
                request.exchangeRate(),
                request.exchangeRateSource(),
                amountBase,
                category,
                request.date(),
                request.notes(),
                actor,
                paidBy,
                request.ownershipType(),
                request.transactionType()
        ));

        List<TransactionSplit> splits = createSplits(familyId, transaction, request);
        if (transaction.getOwnershipType() == TransactionOwnershipType.SHARED) {
            createSettlements(transaction, splits);
        }
        return toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> list(UUID familyId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        return transactionRepository.findByFamilyIdAndActiveTrueOrderByTransactionDateDesc(familyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(UUID familyId, UUID transactionId, UUID userId) {
        familyAccessService.requireMember(familyId, userId);
        return transactionRepository.findByIdAndFamilyIdAndActiveTrue(transactionId, familyId)
                .map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    private List<TransactionSplit> createSplits(UUID familyId, FinancialTransaction transaction, CreateTransactionRequest request) {
        if (request.ownershipType() != TransactionOwnershipType.SHARED) {
            return List.of();
        }
        if (request.splits() == null || request.splits().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Shared transaction requires splits");
        }
        boolean amountMode = request.splits().stream().allMatch(split -> split.shareAmount() != null && split.sharePercentage() == null);
        boolean percentageMode = request.splits().stream().allMatch(split -> split.shareAmount() == null && split.sharePercentage() != null);
        if (!amountMode && !percentageMode) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Use either amount splits or percentage splits for one transaction");
        }
        List<TransactionSplit> splits = new ArrayList<>();
        for (SplitRequest splitRequest : request.splits()) {
            familyAccessService.requireMember(familyId, splitRequest.userId());
            BigDecimal shareAmount = amountMode
                    ? splitRequest.shareAmount()
                    : transaction.getAmountBase().multiply(splitRequest.sharePercentage()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            splits.add(new TransactionSplit(
                    transaction,
                    user(splitRequest.userId()),
                    amountMode ? SplitType.AMOUNT : SplitType.PERCENTAGE,
                    shareAmount,
                    splitRequest.sharePercentage()
            ));
        }
        BigDecimal total = splits.stream().map(TransactionSplit::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(transaction.getAmountBase()) != 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Split total must equal transaction base amount");
        }
        return splitRepository.saveAll(splits);
    }

    private void createSettlements(FinancialTransaction transaction, List<TransactionSplit> splits) {
        List<Settlement> settlements = splits.stream()
                .filter(split -> !split.getUser().getId().equals(transaction.getPaidBy().getId()))
                .map(split -> new Settlement(
                        transaction.getFamily(),
                        split.getUser(),
                        transaction.getPaidBy(),
                        split.getShareAmount(),
                        transaction.getFamily().getBaseCurrency(),
                        transaction
                ))
                .toList();
        settlementRepository.saveAll(settlements);
    }

    private TransactionResponse toResponse(FinancialTransaction transaction) {
        List<TransactionResponse.SplitResponse> splits = splitRepository.findByTransactionId(transaction.getId()).stream()
                .map(split -> new TransactionResponse.SplitResponse(split.getUser().getId(), split.getShareAmount(), split.getSharePercentage()))
                .toList();
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmountOriginal(),
                transaction.getCurrencyOriginal(),
                transaction.getExchangeRate(),
                transaction.getAmountBase(),
                transaction.getPaidBy().getId(),
                transaction.getOwnershipType().name(),
                transaction.getTransactionType().name(),
                transaction.getStatus().name(),
                transaction.getTransactionDate(),
                splits
        );
    }

    private UserAccount user(UUID userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
