package com.boothlens.dto.response.booth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoothQuotationItem {
    private String category;      // 例如: "Structure", "Flooring", "AV Equipment"
    private String description;   // 項目描述
    private int quantity;         // 數量
    private BigDecimal unitPrice; // 單價
    private BigDecimal totalPrice;// 總價 (quantity * unitPrice)
}
