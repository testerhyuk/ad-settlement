package io.github.hyuk.adsettlement.publisher.presentation;

import io.github.hyuk.adsettlement.publisher.business.AdPlacementService;
import io.github.hyuk.adsettlement.publisher.exception.AdPlacementNotFoundException;
import io.github.hyuk.adsettlement.publisher.exception.DuplicatePublisherIdAndPlacementNameException;
import io.github.hyuk.adsettlement.publisher.exception.PublisherIdNotFoundException;
import io.github.hyuk.adsettlement.publisher.presentation.advice.AdPlacementExceptionHandler;
import io.github.hyuk.adsettlement.publisher.presentation.advice.PublisherExceptionHandler;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({
        AdPlacementController.class,
        AdPlacementExceptionHandler.class,
        PublisherExceptionHandler.class
})
class AdPlacementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdPlacementService adPlacementService;

    @Test
    @DisplayName("광고 지면 등록 성공")
    void register() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdPlacementResponse response = new AdPlacementResponse(
                1L,
                101L,
                "상단 배너",
                "ACTIVE",
                createdAt
        );

        when(adPlacementService.register(
                any(AdPlacementRequest.class),
                eq(1L)
        )).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/publishers/{publisherId}/ad-placements", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "placementName": "상단 배너"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.placementId").value(101))
                .andExpect(jsonPath("$.placementName").value("상단 배너"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(adPlacementService).register(
                any(AdPlacementRequest.class),
                eq(1L)
        );
    }

    @Test
    @DisplayName("동일한 퍼블리셔 내에서 광고 지면 이름 중복 시 409 반환")
    void registerDuplicatePlacementName() throws Exception {
        // given
        when(adPlacementService.register(
                any(AdPlacementRequest.class),
                eq(1L)
        )).thenThrow(
                new DuplicatePublisherIdAndPlacementNameException(
                        "이미 등록된 배너 위치입니다"
                )
        );

        // when & then
        mockMvc.perform(post("/api/v1/publishers/{publisherId}/ad-placements", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "placementName": "상단 배너"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 등록된 배너 위치입니다"));

        verify(adPlacementService).register(
                any(AdPlacementRequest.class),
                eq(1L)
        );
    }

    @Test
    @DisplayName("존재하지 않는 퍼블리셔 ID로 등록 시 404 반환")
    void registerPublisherNotFound() throws Exception {
        // given
        when(adPlacementService.register(
                any(AdPlacementRequest.class),
                eq(999L)
        )).thenThrow(
                new PublisherIdNotFoundException("존재하지 않는 퍼블리셔입니다.")
        );

        // when & then
        mockMvc.perform(post("/api/v1/publishers/{publisherId}/ad-placements", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "placementName": "상단 배너"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 퍼블리셔입니다."));

        verify(adPlacementService).register(
                any(AdPlacementRequest.class),
                eq(999L)
        );
    }

    @Test
    @DisplayName("광고 지면 이름 누락 시 400 반환")
    void registerInvalidRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/publishers/{publisherId}/ad-placements", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "placementName": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adPlacementService);
    }

    @Test
    @DisplayName("광고 지면 단건 조회 성공")
    void getAdPlacement() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdPlacementDetailResponse response = new AdPlacementDetailResponse(
                1L,
                101L,
                "테스트 뉴스",
                "https://test.com",
                "상단 배너",
                "ACTIVE",
                createdAt
        );

        when(adPlacementService.getAdPlacement(101L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/ad-placements/{placementId}", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.placementId").value(101))
                .andExpect(jsonPath("$.publisherName").value("테스트 뉴스"))
                .andExpect(jsonPath("$.siteUrl").value("https://test.com"))
                .andExpect(jsonPath("$.placementName").value("상단 배너"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(adPlacementService).getAdPlacement(101L);
    }

    @Test
    @DisplayName("존재하지 않는 광고 지면 조회 시 404 반환")
    void getAdPlacementNotFound() throws Exception {
        // given
        when(adPlacementService.getAdPlacement(999L))
                .thenThrow(
                        new AdPlacementNotFoundException("존재하지 않는 배너 번호입니다")
                );

        // when & then
        mockMvc.perform(get("/api/v1/ad-placements/{placementId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 배너 번호입니다"));

        verify(adPlacementService).getAdPlacement(999L);
    }
}