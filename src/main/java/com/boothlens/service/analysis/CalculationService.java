package com.boothlens.service.analysis;

import com.boothlens.dto.response.booth.BoothAnalysisResponse;
import com.boothlens.dto.response.booth.BoothQuotationCalculation;
import com.boothlens.dto.response.booth.BoothQuotationItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculationService {

    /**
     * 真實的估算邏輯 (根據 AI 分析結果)
     */
    public BoothQuotationCalculation calculateBoothQuotation(BoothAnalysisResponse response) {
        List<BoothQuotationItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        // 1. Structure & Walls (結構與牆面)
        if (response.getBoothSpecs() != null) {
            String structureType = response.getBoothSpecs().getStructureType();
            BigDecimal basePrice = "Custom Wood".equalsIgnoreCase(structureType) ? new BigDecimal("4500.00") : new BigDecimal("2500.00");
            items.add(createItem("Structure", structureType + " Fabrication & Setup", 1, basePrice));
        }

        if (response.getWalls() != null) {
            int wallQty = response.getWalls().getBackWallQty() + response.getWalls().getSideWallQty();
            if (wallQty > 0) {
                BigDecimal wallPrice = response.getWalls().isHasSeamlessFinish() ? new BigDecimal("250.00") : new BigDecimal("100.00");
                items.add(createItem("Walls", "Wall Panels (" + response.getWalls().getMaterial() + ")", wallQty, wallPrice));
            }
        }

        // 2. Flooring (地板)
        if (response.getFlooring() != null) {
            BigDecimal floorPrice = response.getFlooring().isRaisedPlatform() ? new BigDecimal("1200.00") : new BigDecimal("400.00");
            String floorDesc = (response.getFlooring().isRaisedPlatform() ? "Raised Platform with " : "") + response.getFlooring().getMaterial();
            items.add(createItem("Flooring", floorDesc, 1, floorPrice));
        }

        // 3. AV Equipment (影音設備)
        if (response.getAvEquipment() != null) {
            for (BoothAnalysisResponse.AvEquipment av : response.getAvEquipment()) {
                BigDecimal avPrice = av.getItem().toLowerCase().contains("led") ? new BigDecimal("1500.00") : new BigDecimal("350.00");
                items.add(createItem("AV Equipment", av.getItem() + " (" + av.getEstimatedSizeInches() + "\")", av.getQuantity(), avPrice));
            }
        }

        // 4. Furniture (家具)
        if (response.getFurniture() != null) {
            for (BoothAnalysisResponse.Furniture f : response.getFurniture()) {
                BigDecimal furnPrice = f.getHasCustomLogo() != null && f.getHasCustomLogo() ? new BigDecimal("150.00") : new BigDecimal("50.00");
                items.add(createItem("Furniture", f.getItem(), f.getQuantity(), furnPrice));
            }
        }

        // 5. Labor & Logistics (進退場人工與物流 - 估算為硬體成本的 20%)
        for (BoothQuotationItem item : items) {
            subtotal = subtotal.add(item.getTotalPrice());
        }
        BigDecimal laborCost = subtotal.multiply(new BigDecimal("0.20"));
        items.add(createItem("Labor", "Installation & Dismantling (I&D), Logistics", 1, laborCost));
        subtotal = subtotal.add(laborCost);

        // 6. 計算總計
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.07")); // 假設 7% 稅
        BigDecimal total = subtotal.add(tax);

        return new BoothQuotationCalculation(items, subtotal, tax, total);
    }

    /**
     * 測試用的 Dummy Function (Hardcode 數值)
     */
    public BoothQuotationCalculation dummyCalculateBoothQuotation() {
        List<BoothQuotationItem> items = new ArrayList<>();
        items.add(createItem("Design", "3D Design & Engineering Rendering", 1, new BigDecimal("1500.00")));
        items.add(createItem("Structure", "Custom Wood Fabrication & Structural Elements", 1, new BigDecimal("4000.00")));
        items.add(createItem("Flooring", "Raised Floor & Premium Carpet", 1, new BigDecimal("800.00")));
        items.add(createItem("Lighting", "Lighting & Electrical Distribution", 1, new BigDecimal("1200.00")));
        items.add(createItem("Graphics", "Graphics Printing & Signage", 1, new BigDecimal("1000.00")));
        items.add(createItem("Labor", "On-site Installation & Dismantling (Labor)", 1, new BigDecimal("1500.00")));

        BigDecimal subtotal = new BigDecimal("10000.00");
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal total = new BigDecimal("10000.00");

        return new BoothQuotationCalculation(items, subtotal, tax, total);
    }

    private BoothQuotationItem createItem(String category, String desc, int qty, BigDecimal unitPrice) {
        BigDecimal total = unitPrice.multiply(new BigDecimal(qty));
        return new BoothQuotationItem(category, desc, qty, unitPrice, total);
    }
}
