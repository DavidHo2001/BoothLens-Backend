package com.boothlens.service;

import com.boothlens.dto.request.admin.AnnouncementUpsertRequestDto;
import com.boothlens.dto.response.announcement.AnnouncementDto;
import com.boothlens.entity.Announcement;
import com.boothlens.repository.AnnouncementRepository;
import com.boothlens.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AnnouncementDto getAnnouncementById(Integer id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        return toDto(announcement);
    }

    @Transactional
    public AnnouncementDto createAnnouncement(AnnouncementUpsertRequestDto request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle().trim());
        announcement.setBody(request.getBody().trim());
        announcement.setActive(Boolean.TRUE.equals(request.getActive()));
        announcement.setCreatedBy(SecurityUtils.getCurrentUserId());
        return toDto(announcementRepository.saveAndFlush(announcement));
    }

    @Transactional
    public AnnouncementDto updateAnnouncement(Integer id, AnnouncementUpsertRequestDto request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        announcement.setTitle(request.getTitle().trim());
        announcement.setBody(request.getBody().trim());
        announcement.setActive(Boolean.TRUE.equals(request.getActive()));
        return toDto(announcementRepository.saveAndFlush(announcement));
    }

    @Transactional
    public void deleteAnnouncement(Integer id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
        announcementRepository.delete(announcement);
    }

    private AnnouncementDto toDto(Announcement announcement) {
        return AnnouncementDto.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .body(announcement.getBody())
                .active(announcement.isActive())
                .createdBy(announcement.getCreatedBy())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }
}
