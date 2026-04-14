package com.boothlens.dto.request.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenRouterRequest {
    
    private String model;
    private List<Message> messages;
    
    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    // --- 內部靜態類別 (Inner Static Classes) 讓結構一目了然 ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        // content 可以是單純的 String (System Prompt)，也可以是 List<ContentPart> (User Prompt 包含圖片)
        // 所以這裡宣告為 Object，Jackson 會自動根據實際傳入的型別轉換為 JSON String 或 JSON Array
        private Object content; 
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContentPart {
        private String type; // "text" 或 "image_url"
        private String text;
        
        @JsonProperty("image_url")
        private ImageUrl imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUrl {
        private String url;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFormat {
        private String type;
    }
}
