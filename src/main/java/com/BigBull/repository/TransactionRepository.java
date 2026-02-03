package com.BigBull.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAssetId(Long assetId);

    List<Transaction> findByUsername(String username);

    List<Transaction> findByUsernameAndAsset(String username, Asset asset);

    @Query("SELECT t FROM Transaction t WHERE t.asset.symbol = :symbol")
    List<Transaction> findByAssetSymbol(@Param("symbol") String symbol);
}
