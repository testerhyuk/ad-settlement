package io.github.hyuk.adsettlement.publisher.presentation;

import io.github.hyuk.adsettlement.publisher.business.PublisherService;
import io.github.hyuk.adsettlement.publisher.exception.DuplicateSiteUrlException;
import io.github.hyuk.adsettlement.publisher.exception.PublisherIdNotFoundException;
import io.github.hyuk.adsettlement.publisher.presentation.advice.PublisherExceptionHandler;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({
        PublisherController.class,
        PublisherExceptionHandler.class
})
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublisherService publisherService;

    @Test
    @DisplayName("퍼블리셔 등록 성공")
    void register() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        PublisherResponse response = new PublisherResponse(
                1L,
                "테스트 뉴스",
                "https://test.com",
                createdAt
        );

        when(publisherService.register(any(PublisherRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "publisherName": "테스트 뉴스",
                                    "siteUrl": "https://test.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.publisherName").value("테스트 뉴스"))
                .andExpect(jsonPath("$.siteUrl").value("https://test.com"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(publisherService).register(any(PublisherRequest.class));
    }

    @Test
    @DisplayName("중복된 사이트 URL로 등록 시 409 반환")
    void registerDuplicateSiteUrl() throws Exception {
        // given
        when(publisherService.register(any(PublisherRequest.class)))
                .thenThrow(new DuplicateSiteUrlException("이미 등록된 퍼블리셔입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "publisherName": "테스트 뉴스",
                                    "siteUrl": "https://test.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 등록된 퍼블리셔입니다."));

        verify(publisherService).register(any(PublisherRequest.class));
    }

    @Test
    @DisplayName("퍼블리셔 이름 누락 시 400 반환")
    void registerInvalidRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "publisherName": "",
                                    "siteUrl": "https://test.com"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(publisherService);
    }

    @Test
    @DisplayName("퍼블리셔 단건 조회 성공")
    void getPublisher() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        PublisherResponse response = new PublisherResponse(
                1L,
                "테스트 뉴스",
                "https://test.com",
                createdAt
        );

        when(publisherService.getPublisher(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/publishers/{publisherId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.publisherName").value("테스트 뉴스"))
                .andExpect(jsonPath("$.siteUrl").value("https://test.com"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(publisherService).getPublisher(1L);
    }

    @Test
    @DisplayName("존재하지 않는 퍼블리셔 조회 시 404 반환")
    void getPublisherNotFound() throws Exception {
        // given
        when(publisherService.getPublisher(999L))
                .thenThrow(new PublisherIdNotFoundException("존재하지 않는 퍼블리셔입니다."));

        // when & then
        mockMvc.perform(get("/api/v1/publishers/{publisherId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 퍼블리셔입니다."));

        verify(publisherService).getPublisher(999L);
    }

    @Test
    @DisplayName("퍼블리셔와 광고 지면 목록 조회 성공")
    void getPublisherWithAdPlacementList() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdPlacementResponse adPlacement1 = new AdPlacementResponse(
                1L,
                101L,
                "상단 배너",
                "ACTIVE",
                createdAt
        );

        AdPlacementResponse adPlacement2 = new AdPlacementResponse(
                1L,
                102L,
                "본문 중간",
                "ACTIVE",
                createdAt
        );

        PublisherDetailResponse response = new PublisherDetailResponse(
                1L,
                "테스트 뉴스",
                "https://test.com",
                createdAt,
                List.of(adPlacement1, adPlacement2)
        );

        when(publisherService.getPublisherWithAdPlacementList(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/publishers/{publisherId}/ad-placements", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.publisherName").value("테스트 뉴스"))
                .andExpect(jsonPath("$.siteUrl").value("https://test.com"))
                .andExpect(jsonPath("$.adPlacements.length()").value(2))
                .andExpect(jsonPath("$.adPlacements[0].placementId").value(101))
                .andExpect(jsonPath("$.adPlacements[0].placementName").value("상단 배너"))
                .andExpect(jsonPath("$.adPlacements[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.adPlacements[1].placementId").value(102))
                .andExpect(jsonPath("$.adPlacements[1].placementName").value("본문 중간"));

        verify(publisherService).getPublisherWithAdPlacementList(1L);
    }

    @Test
    @DisplayName("등록된 광고 지면이 없는 퍼블리셔 조회 시 빈 목록 반환")
    void getPublisherWithEmptyAdPlacementList() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        PublisherDetailResponse response = new PublisherDetailResponse(
                1L,
                "신규 뉴스",
                "https://newsite.com",
                createdAt,
                List.of()
        );

        when(publisherService.getPublisherWithAdPlacementList(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/publishers/{publisherId}/ad-placements", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.publisherName").value("신규 뉴스"))
                .andExpect(jsonPath("$.adPlacements").isEmpty());

        verify(publisherService).getPublisherWithAdPlacementList(1L);
    }

    @Test
    @DisplayName("존재하지 않는 퍼블리셔의 광고 지면 조회 시 404 반환")
    void getPublisherWithAdPlacementListNotFound() throws Exception {
        // given
        when(publisherService.getPublisherWithAdPlacementList(999L))
                .thenThrow(new PublisherIdNotFoundException("존재하지 않는 퍼블리셔입니다."));

        // when & then
        mockMvc.perform(get("/api/v1/publishers/{publisherId}/ad-placements", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 퍼블리셔입니다."));

        verify(publisherService).getPublisherWithAdPlacementList(999L);
    }
}