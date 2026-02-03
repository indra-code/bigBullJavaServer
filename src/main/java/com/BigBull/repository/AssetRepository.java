package com.BigBull.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BigBull.entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);
    List<Asset> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(String symbol, String name);

    boolean existsBySymbol(String symbol);
}
