package io.github.hyuk.adsettlement.publisher.business;

import io.github.hyuk.adsettlement.publisher.exception.AdPlacementNotFoundException;
import io.github.hyuk.adsettlement.publisher.exception.DuplicatePublisherIdAndPlacementNameException;
import io.github.hyuk.adsettlement.publisher.exception.PublisherIdNotFoundException;
import io.github.hyuk.adsettlement.publisher.implement.AdPlacementAppender;
import io.github.hyuk.adsettlement.publisher.implement.AdPlacementReader;
import io.github.hyuk.adsettlement.publisher.implement.PublisherReader;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.Publisher;
import io.github.hyuk.adsettlement.publisher.persistence.enums.PlacementStatus;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdPlacementServiceTest {

    @Mock
    private AdPlacementReader adPlacementReader;

    @Mock
    private AdPlacementAppender adPlacementAppender;

    @Mock
    private PublisherReader publisherReader;

    @InjectMocks
    private AdPlacementService adPlacementService;

    @Test
    @DisplayName("광고 지면 등록 성공")
    void register() {
        // given
        Publisher publisher = mock(Publisher.class);
        when(publisher.getPublisherId()).thenReturn(1L);

        AdPlacementRequest request = new AdPlacementRequest(
                "상단 배너"
        );

        when(publisherReader.getById(1L))
                .thenReturn(publisher);

        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdPlacement savedAdPlacement = mock(AdPlacement.class);

        when(savedAdPlacement.getPublisher()).thenReturn(publisher);
        when(savedAdPlacement.getPlacementId()).thenReturn(101L);
        when(savedAdPlacement.getPlacementName()).thenReturn("상단 배너");
        when(savedAdPlacement.getStatus()).thenReturn(PlacementStatus.ACTIVE);
        when(savedAdPlacement.getCreatedAt()).thenReturn(createdAt);

        when(adPlacementAppender.append(any(AdPlacement.class)))
                .thenReturn(savedAdPlacement);

        // when
        AdPlacementResponse response =
                adPlacementService.register(request, 1L);

        // then
        assertEquals(1L, response.publisherId());
        assertEquals(101L, response.placementId());
        assertEquals("상단 배너", response.placementName());
        assertEquals("ACTIVE", response.status());
        assertEquals(createdAt, response.createdAt());

        verify(publisherReader).getById(1L);
        verify(adPlacementReader).validateNotExists(1L, "상단 배너");
        verify(adPlacementAppender).append(any(AdPlacement.class));
    }

    @Test
    @DisplayName("동일한 퍼블리셔 내에서 광고 지면 이름 중복 시 예외 발생")
    void registerDuplicatePlacementName() {
        // given
        Publisher publisher = mock(Publisher.class);

        when(publisher.getPublisherId()).thenReturn(1L);

        AdPlacementRequest request = new AdPlacementRequest(
                "상단 배너"
        );

        when(publisherReader.getById(1L))
                .thenReturn(publisher);

        doThrow(new DuplicatePublisherIdAndPlacementNameException(
                "이미 등록된 배너 위치입니다."
        )).when(adPlacementReader).validateNotExists(1L, "상단 배너");

        // when & then
        assertThrows(
                DuplicatePublisherIdAndPlacementNameException.class,
                () -> adPlacementService.register(request, 1L)
        );

        verify(publisherReader).getById(1L);
        verify(adPlacementReader).validateNotExists(1L, "상단 배너");
        verify(adPlacementAppender, never()).append(any(AdPlacement.class));
    }

    @Test
    @DisplayName("존재하지 않는 퍼블리셔 ID로 광고 지면 등록 시 예외 발생")
    void registerPublisherNotFound() {
        // given
        AdPlacementRequest request = new AdPlacementRequest(
                "상단 배너"
        );

        when(publisherReader.getById(999L))
                .thenThrow(new PublisherIdNotFoundException(
                        "존재하지 않는 퍼블리셔입니다."
                ));

        // when & then
        assertThrows(
                PublisherIdNotFoundException.class,
                () -> adPlacementService.register(request, 999L)
        );

        verify(publisherReader).getById(999L);
        verify(adPlacementReader, never()).validateNotExists(anyLong(), anyString());
        verify(adPlacementAppender, never()).append(any(AdPlacement.class));
    }

    @Test
    @DisplayName("광고 지면 단건 조회 성공")
    void getAdPlacement() {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        Publisher publisher = mock(Publisher.class);

        when(publisher.getPublisherId()).thenReturn(1L);
        when(publisher.getPublisherName()).thenReturn("테스트 뉴스");
        when(publisher.getSiteUrl()).thenReturn("https://test.com");

        AdPlacement adPlacement = mock(AdPlacement.class);

        when(adPlacement.getPlacementId()).thenReturn(101L);
        when(adPlacement.getPublisher()).thenReturn(publisher);
        when(adPlacement.getPlacementName()).thenReturn("상단 배너");
        when(adPlacement.getStatus()).thenReturn(PlacementStatus.ACTIVE);
        when(adPlacement.getCreatedAt()).thenReturn(createdAt);

        when(adPlacementReader.getAdPlacement(101L))
                .thenReturn(adPlacement);

        // when
        AdPlacementDetailResponse response =
                adPlacementService.getAdPlacement(101L);

        // then
        assertEquals(1L, response.publisherId());
        assertEquals(101L, response.placementId());
        assertEquals("테스트 뉴스", response.publisherName());
        assertEquals("https://test.com", response.siteUrl());
        assertEquals("상단 배너", response.placementName());
        assertEquals("ACTIVE", response.status());
        assertEquals(createdAt, response.createdAt());

        verify(adPlacementReader).getAdPlacement(101L);
    }

    @Test
    @DisplayName("존재하지 않는 광고 지면 조회 시 예외 발생")
    void getAdPlacementNotFound() {
        // given
        when(adPlacementReader.getAdPlacement(999L))
                .thenThrow(new AdPlacementNotFoundException(
                        "존재하지 않는 배너 번호입니다."
                ));

        // when & then
        assertThrows(
                AdPlacementNotFoundException.class,
                () -> adPlacementService.getAdPlacement(999L)
        );

        verify(adPlacementReader).getAdPlacement(999L);
    }
}