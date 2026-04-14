package com.boothlens.dto.request.booth;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoothAnalysisRequest {
    private double boothWidth;
    private double boothLength;
    private double boothHeight;
    private String boothSizeUnit; // e.g., "m", "ft"
    private String requestInfo; //E.g. There are xxx in the booth...
}
