package com.boothlens.dto.response.booth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoothQuotationCalculation {
    private List<BoothQuotationItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
}
