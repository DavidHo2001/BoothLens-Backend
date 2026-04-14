package com.boothlens.dto.response.booth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoothAnalysisResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoothSpecs {
        @JsonProperty("estimated_size")
        private String estimatedSize;

        @JsonProperty("structure_type")
        private String structureType;

        @JsonProperty("open_sides")
        private int openSides;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Flooring {
        private String material;
        private String color;

        @JsonProperty("is_raised_platform")
        private boolean isRaisedPlatform;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Walls {
        private String material;

        @JsonProperty("has_seamless_finish")
        private boolean hasSeamlessFinish;

        @JsonProperty("back_wall_qty")
        private int backWallQty;

        @JsonProperty("side_wall_qty")
        private int sideWallQty;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvEquipment {
        private String item;

        @JsonProperty("estimated_size_inches")
        private int estimatedSizeInches;

        private int quantity;

        private String mounting; // optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Furniture {
        private String item;
        private int quantity;
        private String color; // optional

        @JsonProperty("has_custom_logo")
        private Boolean hasCustomLogo; // optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lighting {
        private String type;
        private Integer quantity; // optional

        @JsonProperty("quantity_estimated_meters")
        private Integer quantityEstimatedMeters; // optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphicsAndBranding {
        private String type;
        private int quantity;

        @JsonProperty("estimated_size")
        private String estimatedSize; // optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiConfidence {
        @JsonProperty("overall_score")
        private double overallScore;

        @JsonProperty("unseen_assumptions")
        private List<String> unseenAssumptions;
    }

    @JsonProperty("booth_specs")
    private BoothSpecs boothSpecs;

    private Flooring flooring;

    private Walls walls;

    @JsonProperty("av_equipment")
    private List<AvEquipment> avEquipment;

    private List<Furniture> furniture;

    private List<Lighting> lighting;

    @JsonProperty("graphics_and_branding")
    private List<GraphicsAndBranding> graphicsAndBranding;

    @JsonProperty("ai_confidence")
    private AiConfidence aiConfidence;
}
