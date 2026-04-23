package com.boothlens.dto.response.announcement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {
    private Integer id;
    private String title;
    private String body;
    private boolean active;
    private Integer createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
