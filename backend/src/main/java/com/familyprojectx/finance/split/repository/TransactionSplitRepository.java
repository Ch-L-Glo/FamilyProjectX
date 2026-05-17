package com.familyprojectx.finance.split.repository;

import com.familyprojectx.finance.split.entity.TransactionSplit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionSplitRepository extends JpaRepository<TransactionSplit, UUID> {

    List<TransactionSplit> findByTransactionId(UUID transactionId);
}
