package com.BigBull.repository;

import com.BigBull.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    List<Asset> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(String symbol, String name);
}
