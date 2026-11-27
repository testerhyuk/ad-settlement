package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.exception.DuplicateAdvertisementNameException;
import io.github.hyuk.adsettlement.advertising.implement.AdvertisementAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertisementReader;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.persistence.enums.AdvertisementStatus;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {
    @Mock
    private AdvertisementReader advertisementReader;
    @Mock
    private AdvertisementAppender advertisementAppender;
    @Mock
    private AdvertiserReader advertiserReader;
    @Mock
    Advertiser advertiser;
    @InjectMocks
    private AdvertisementService advertisementService;

    @Test
    @DisplayName("광고 등록 성공")
    void register() {
        // given
        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                "V",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10_000),
                AdvertisementStatus.ACTIVE,
                Instant.parse("2026-11-27T09:00:00Z"),
                Instant.parse("2026-11-29T09:00:00Z")
        );

        when(advertiser.getAdvertiserId()).thenReturn(1L);
        when(advertiserReader.getById(1L)).thenReturn(advertiser);
        when(advertisementAppender.append(any(Advertisement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        AdvertisementResponse response = advertisementService.register(request);

        // then
        assertEquals("V", response.advertisementName());
        assertEquals(1L, response.advertiserId());
        assertEquals(BigDecimal.valueOf(1_000_000), response.budgetAmount());
        assertEquals(BigDecimal.valueOf(100), response.impressionPrice());
        assertEquals(BigDecimal.valueOf(10_000), response.conversionPrice());
        assertEquals("ACTIVE", response.status());
        assertEquals(request.startedAt(), response.startedAt());
        assertEquals(request.expiredAt(), response.expiredAt());
        assertNotNull(response.createdAt());

        verify(advertisementReader).validateNotExists(1L, "V");
        verify(advertisementAppender).append(any(Advertisement.class));
    }

    @Test
    @DisplayName("동일 광고주에게 이미 등록된 광고 이름으로 등록하면 예외 발생")
    void registerDuplicateAdvertisementName() {
        // given
        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                "V",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10_000),
                AdvertisementStatus.ACTIVE,
                Instant.parse("2026-11-27T09:00:00Z"),
                Instant.parse("2026-11-29T09:00:00Z")
        );

        when(advertiserReader.getById(1L))
                .thenReturn(advertiser);

        doThrow(new DuplicateAdvertisementNameException("이미 등록된 광고입니다."))
                .when(advertisementReader)
                .validateNotExists(1L, "V");

        // when & then
        assertThrows(
                DuplicateAdvertisementNameException.class,
                () -> advertisementService.register(request)
        );

        verify(advertiserReader).getById(1L);
        verify(advertisementReader).validateNotExists(1L, "V");

        verify(advertisementAppender, never())
                .append(any(Advertisement.class));
    }

    @Test
    @DisplayName("존재하지 않는 광고주로 광고 등록 시 예외 발생")
    void registerWithNotFoundAdvertiser() {
        // given
        AdvertisementRequest request = new AdvertisementRequest(
                999L,
                "V",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10_000),
                AdvertisementStatus.ACTIVE,
                Instant.parse("2026-11-27T09:00:00Z"),
                Instant.parse("2026-11-29T09:00:00Z")
        );

        when(advertiserReader.getById(999L))
                .thenThrow(new IllegalArgumentException("존재하지 않는 광고주입니다."));

        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> advertisementService.register(request)
        );

        verify(advertiserReader).getById(999L);

        verify(advertisementReader, never())
                .validateNotExists(anyLong(), anyString());

        verify(advertisementAppender, never())
                .append(any(Advertisement.class));
    }
}