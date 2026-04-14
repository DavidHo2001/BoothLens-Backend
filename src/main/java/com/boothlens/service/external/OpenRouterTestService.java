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
public class OpenRouterTestService {
    
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenRouterRequest sendRequestWithImage(MultipartFile[] images, String systemPrompt, String userPromptText) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apikey);

            Message systemMessage = Message.builder()
                    .role(systemRole)
                    .content(systemPrompt)
                    .build();

            List<ContentPart> userContentParts = new ArrayList<>();

            userContentParts.add(ContentPart.builder()
                    .type(textType)
                    .text(userPromptText)
                    .build());

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

            Message userMessage = Message.builder()
                    .role("user")
                    .content(userContentParts)
                    .build();

            return OpenRouterRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(systemMessage, userMessage))
                    .responseFormat(new ResponseFormat("json_object"))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build OpenRouter test request", e);
        }
    }
}
