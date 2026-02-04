package com.BigBull.service;

import com.BigBull.dto.AssetSummary;
import com.BigBull.dto.PortfolioSummary;
import com.BigBull.entity.Asset;
import com.BigBull.entity.Wallet;
import com.BigBull.repository.AssetRepository;
import com.BigBull.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetService assetService;

    public PortfolioSummary getPortfolioSummary(String username) {
        // Get wallet
        Optional<Wallet> walletOpt = walletRepository.findByUsername(username);
        if (!walletOpt.isPresent()) {
            throw new RuntimeException("Wallet not found for username: " + username);
        }
        Wallet wallet = walletOpt.get();

        // Get all assets with quantity > 0
        List<Asset> assets = assetRepository.findAll();
        List<AssetSummary> assetSummaries = new ArrayList<>();
        double portfolioValue = 0;
        double totalCostValue = 0;

        for (Asset asset : assets) {
            if (asset.getQuantity() != null && asset.getQuantity() > 0) {
                AssetSummary summary = assetService.getAssetSummary(asset);
                assetSummaries.add(summary);
                portfolioValue += summary.getTotalValue();
                totalCostValue += summary.getTotalCostValue();
            }
        }

        // Calculate total gain
        double totalGain = portfolioValue - totalCostValue;
        double gainPercentage = (totalCostValue > 0) ? (totalGain / totalCostValue) * 100 : 0;

        // Create portfolio summary
        return new PortfolioSummary(
                username,
                wallet.getBalance(),
                wallet.getTotalInvested(),
                wallet.getTotalWithdrawn(),
                portfolioValue,
                totalGain,
                gainPercentage,
                assetSummaries
        );
    }

    public List<Asset> getPortfolioList() {
        return assetRepository.findAll();
    }
}
