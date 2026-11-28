package io.github.hyuk.adsettlement.publisher.business;

import io.github.hyuk.adsettlement.publisher.exception.DuplicateSiteUrlException;
import io.github.hyuk.adsettlement.publisher.exception.PublisherIdNotFoundException;
import io.github.hyuk.adsettlement.publisher.implement.AdPlacementReader;
import io.github.hyuk.adsettlement.publisher.implement.PublisherAppender;
import io.github.hyuk.adsettlement.publisher.implement.PublisherReader;
import io.github.hyuk.adsettlement.publisher.persistence.AdPlacement;
import io.github.hyuk.adsettlement.publisher.persistence.Publisher;
import io.github.hyuk.adsettlement.publisher.persistence.enums.PlacementStatus;
import io.github.hyuk.adsettlement.publisher.presentation.dto.AdPlacementResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherDetailResponse;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherRequest;
import io.github.hyuk.adsettlement.publisher.presentation.dto.PublisherResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private PublisherReader publisherReader;

    @Mock
    private PublisherAppender publisherAppender;

    @Mock
    private AdPlacementReader adPlacementReader;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    @DisplayName("퍼블리셔 등록 성공")
    void register() {
        // given
        PublisherRequest request = new PublisherRequest(
                "테스트 뉴스",
                "https://test.com"
        );

        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        Publisher savedPublisher = mock(Publisher.class);

        when(savedPublisher.getPublisherId()).thenReturn(1L);
        when(savedPublisher.getPublisherName()).thenReturn("테스트 뉴스");
        when(savedPublisher.getSiteUrl()).thenReturn("https://test.com");
        when(savedPublisher.getCreatedAt()).thenReturn(createdAt);

        when(publisherAppender.append(any(Publisher.class)))
                .thenReturn(savedPublisher);

        // when
        PublisherResponse response = publisherService.register(request);

        // then
        assertEquals(1L, response.publisherId());
        assertEquals("테스트 뉴스", response.publisherName());
        assertEquals("https://test.com", response.siteUrl());
        assertEquals(createdAt, response.createdAt());

        verify(publisherReader).validateNotExists("https://test.com");
        verify(publisherAppender).append(any(Publisher.class));
    }

    @Test
    @DisplayName("이미 등록된 사이트 URL로 등록 시 예외 발생")
    void registerDuplicateSiteUrl() {
        // given
        PublisherRequest request = new PublisherRequest(
                "테스트 뉴스",
                "https://test.com"
        );

        doThrow(new DuplicateSiteUrlException("이미 등록된 퍼블리셔입니다."))
                .when(publisherReader)
                .validateNotExists("https://test.com");

        // when & then
        assertThrows(
                DuplicateSiteUrlException.class,
                () -> publisherService.register(request)
        );

        verify(publisherReader).validateNotExists("https://test.com");
        verify(publisherAppender, never()).append(any(Publisher.class));
    }

    @Test
    @DisplayName("퍼블리셔 단건 조회 성공")
    void getPublisher() {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        Publisher publisher = mock(Publisher.class);

        when(publisher.getPublisherId()).thenReturn(1L);
        when(publisher.getPublisherName()).thenReturn("테스트 뉴스");
        when(publisher.getSiteUrl()).thenReturn("https://test.com");
        when(publisher.getCreatedAt()).thenReturn(createdAt);

        when(publisherReader.getById(1L))
                .thenReturn(publisher);

        // when
        PublisherResponse response = publisherService.getPublisher(1L);

        // then
        assertEquals(1L, response.publisherId());
        assertEquals("테스트 뉴스", response.publisherName());
        assertEquals("https://test.com", response.siteUrl());
        assertEquals(createdAt, response.createdAt());

        verify(publisherReader).getById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 퍼블리셔 조회 시 예외 발생")
    void getPublisherNotFound() {
        // given
        when(publisherReader.getById(999L))
                .thenThrow(new PublisherIdNotFoundException("존재하지 않는 퍼블리셔입니다."));

        // when & then
        assertThrows(
                PublisherIdNotFoundException.class,
                () -> publisherService.getPublisher(999L)
        );

        verify(publisherReader).getById(999L);
    }

    @Test
    @DisplayName("퍼블리셔와 광고 지면 목록 조회 성공")
    void getPublisherWithAdPlacementList() {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        Publisher publisher = mock(Publisher.class);

        when(publisher.getPublisherId()).thenReturn(1L);
        when(publisher.getPublisherName()).thenReturn("테스트 뉴스");
        when(publisher.getSiteUrl()).thenReturn("https://test.com");
        when(publisher.getCreatedAt()).thenReturn(createdAt);

        AdPlacement adPlacement = mock(AdPlacement.class);

        when(adPlacement.getPublisher()).thenReturn(publisher);
        when(adPlacement.getPlacementId()).thenReturn(101L);
        when(adPlacement.getPlacementName()).thenReturn("상단 배너");
        when(adPlacement.getStatus()).thenReturn(PlacementStatus.ACTIVE);
        when(adPlacement.getCreatedAt()).thenReturn(createdAt);

        when(publisherReader.getById(1L))
                .thenReturn(publisher);

        when(adPlacementReader.getAdPlacementList(1L))
                .thenReturn(List.of(adPlacement));

        // when
        PublisherDetailResponse response =
                publisherService.getPublisherWithAdPlacementList(1L);

        // then
        assertEquals(1L, response.publisherId());
        assertEquals("테스트 뉴스", response.publisherName());
        assertEquals("https://test.com", response.siteUrl());
        assertEquals(createdAt, response.createdAt());

        assertEquals(1, response.adPlacements().size());

        AdPlacementResponse placementResponse = response.adPlacements().get(0);

        assertEquals(1L, placementResponse.publisherId());
        assertEquals(101L, placementResponse.placementId());
        assertEquals("상단 배너", placementResponse.placementName());
        assertEquals("ACTIVE", placementResponse.status());
        assertEquals(createdAt, placementResponse.createdAt());

        verify(publisherReader).getById(1L);
        verify(adPlacementReader).getAdPlacementList(1L);
    }

    @Test
    @DisplayName("등록된 광고 지면이 없는 경우 빈 목록 반환")
    void getPublisherWithEmptyAdPlacementList() {
        // given
        Publisher publisher = mock(Publisher.class);

        when(publisher.getPublisherId()).thenReturn(1L);
        when(publisher.getPublisherName()).thenReturn("신규 뉴스");
        when(publisher.getSiteUrl()).thenReturn("https://newsite.com");

        when(publisherReader.getById(1L))
                .thenReturn(publisher);

        when(adPlacementReader.getAdPlacementList(1L))
                .thenReturn(List.of());

        // when
        PublisherDetailResponse response =
                publisherService.getPublisherWithAdPlacementList(1L);

        // then
        assertEquals(1L, response.publisherId());
        assertEquals("신규 뉴스", response.publisherName());
        assertTrue(response.adPlacements().isEmpty());

        verify(publisherReader).getById(1L);
        verify(adPlacementReader).getAdPlacementList(1L);
    }

    @Test
    @DisplayName("존재하지 않는 퍼블리셔의 광고 지면 목록 조회 시 예외 발생")
    void getPublisherWithAdPlacementListNotFound() {
        // given
        when(publisherReader.getById(999L))
                .thenThrow(new PublisherIdNotFoundException("존재하지 않는 퍼블리셔입니다."));

        // when & then
        assertThrows(
                PublisherIdNotFoundException.class,
                () -> publisherService.getPublisherWithAdPlacementList(999L)
        );

        verify(publisherReader).getById(999L);
        verify(adPlacementReader, never()).getAdPlacementList(anyLong());
    }
}