package com.boothlens.service.external;

import com.boothlens.dto.request.openrouter.OpenRouterRequest;
import com.boothlens.dto.request.openrouter.OpenRouterRequest.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class OpenRouterService {
    
    @Value("${openrouter.api-key}")
    private String apikey;
    
    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.request-url}")
    private String requestUrl;

    //fixed string for OpenRouter request
    private String imageType = "image_url";
    private String textType = "text";
    private String systemRole = "system";
    private String userRole = "user";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendRequestWithImage(MultipartFile[] images, String systemPrompt, String userPromptText) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apikey);
            // OpenRouter 官方建議加入這兩個 Header 標識你的 App
            // headers.set("HTTP-Referer", "http://localhost:8080"); 
            // headers.set("X-Title", "BoothLens");

            // 1. 構建 System Message (純文字)
            Message systemMessage = Message.builder()
                    .role(systemRole)
                    .content(systemPrompt)
                    .build();

            // 2. 構建 User Message 內容 (包含文字與多張圖片)
            List<ContentPart> userContentParts = new ArrayList<>();
            
            // 加入 User Prompt 文字
            userContentParts.add(ContentPart.builder()
                    .type(textType)
                    .text(userPromptText)
                    .build());

            // 處理圖片：將 MultipartFile 轉為 Base64 Data URL
            if (images != null) {
                for (MultipartFile image : images) {
                    if (image.isEmpty()) continue;
                    
                    String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                    String mimeType = image.getContentType() != null ? image.getContentType() : "image/jpeg";
                    String dataUrl = "data:" + mimeType + ";base64," + base64Image;

                    userContentParts.add(ContentPart.builder()
                            .type(imageType)
                            .imageUrl(new ImageUrl(dataUrl))
                            .build());
                }
            }

            // 將 ContentPart 放入 User Message
            Message userMessage = Message.builder()
                    .role("user")
                    .content(userContentParts)
                    .build();

            // 3. 組合最終 Request Body DTO
            OpenRouterRequest requestBody = OpenRouterRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(systemMessage, userMessage))
                    .responseFormat(new ResponseFormat("json_object")) // 強制 AI 輸出 JSON
                    .build();
            

            // 使用 DTO 作為 HttpEntity 的 Body
            HttpEntity<OpenRouterRequest> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to OpenRouter with model: {}, images count: {}", model, images != null ? images.length : 0);
            
            // 4. 發送請求 (注意這裡使用了 requestUrl)
            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 解析 OpenRouter 回傳的 JSON
                JsonNode root = objectMapper.readTree(response.getBody());
                String aiMessage = root.path("choices").get(0).path("message").path("content").asText();
                
                // 清理可能出現的 Markdown 標籤
                return cleanJsonString(aiMessage);
            } else {
                log.error("Failed to get valid response from OpenRouter: {}", response.getStatusCode());
                throw new RuntimeException("OpenRouter API error");
            }

        } catch (Exception e) {
            log.error("Error communicating with OpenRouter: ", e);
            throw new RuntimeException("Failed to analyze booth images", e);
        }
    }

    /**
     * 防禦性設計：AI 有時即使被要求，仍會輸出 ```json { ... } ``` 格式
     * 這個方法確保我們只拿到純 JSON 字串，避免 Backend 解析失敗
     */
    private String cleanJsonString(String raw) {
        if (raw == null) return null;
        String cleaned = raw.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }
}
