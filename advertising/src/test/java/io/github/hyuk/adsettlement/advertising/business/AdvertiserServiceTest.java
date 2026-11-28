package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.exception.AdvertiserNotFoundException;
import io.github.hyuk.adsettlement.advertising.exception.DuplicateBusinessRegistrationNumberException;
import io.github.hyuk.adsettlement.advertising.implement.AdvertisementReader;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertisement;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.persistence.enums.AdvertisementStatus;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementResponse;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserDetailResponse;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertiserServiceTest {
    @Mock
    private AdvertiserReader advertiserReader;

    @Mock
    private AdvertiserAppender advertiserAppender;

    @Mock
    private AdvertisementReader advertisementReader;

    @InjectMocks
    private AdvertiserService advertiserService;

    @Test
    @DisplayName("광고주 등록 성공")
    void register() {
        // given
        AdvertiserRequest request = new AdvertiserRequest("A", "123-456");

        when(advertiserAppender.append(any(Advertiser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        AdvertiserResponse response = advertiserService.register(request);

        // then
        assertEquals("A", response.advertiserName());
        assertEquals("123-456", response.businessRegistrationNumber());
        assertNotNull(response.createdAt());

        verify(advertiserReader).validateNotExists("123-456");
        verify(advertiserAppender).append(any(Advertiser.class));
    }

    @Test
    @DisplayName("이미 등록된 사업자등록번호로 광고주 등록 시 예외 발생")
    void checkDuplicateBusinessRegistrationNumberException() {
        // given
        AdvertiserRequest request = new AdvertiserRequest("A", "123-456");

        doThrow(new DuplicateBusinessRegistrationNumberException(
                "이미 등록된 사업등록자번호입니다."
        )).when(advertiserReader)
                .validateNotExists(request.businessRegistrationNumber());

        // when & then
        assertThrows(
                DuplicateBusinessRegistrationNumberException.class,
                () -> advertiserService.register(request)
        );

        verify(advertiserAppender, never()).append(any(Advertiser.class));
    }

    @Test
    @DisplayName("광고주 단건 조회 성공")
    void getAdvertiser() {
        // given
        Advertiser advertiser = mock(Advertiser.class);

        when(advertiser.getAdvertiserId()).thenReturn(1L);
        when(advertiser.getAdvertiserName()).thenReturn("A");
        when(advertiser.getBusinessRegistrationNumber()).thenReturn("1234567890");

        when(advertiserReader.getById(1L))
                .thenReturn(advertiser);

        // when
        AdvertiserResponse response = advertiserService.getAdvertiser(1L);

        // then
        assertEquals(1L, response.advertiserId());
        assertEquals("A", response.advertiserName());
        assertEquals("1234567890", response.businessRegistrationNumber());

        verify(advertiserReader).getById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 광고주 조회 시 예외 발생")
    void getAdvertiserNotFound() {
        // given
        when(advertiserReader.getById(999L))
                .thenThrow(new AdvertiserNotFoundException("존재하지 않는 광고주입니다."));

        // when & then
        assertThrows(
                AdvertiserNotFoundException.class,
                () -> advertiserService.getAdvertiser(999L)
        );
    }

    @Test
    @DisplayName("광고주와 소속 광고 목록 조회 성공")
    void getAdvertiserWithAdvertisementList() {
        // given
        Advertiser advertiser = mock(Advertiser.class);

        when(advertiser.getAdvertiserId()).thenReturn(1L);
        when(advertiser.getAdvertiserName()).thenReturn("A");
        when(advertiser.getBusinessRegistrationNumber()).thenReturn("1234567890");

        when(advertiserReader.getById(1L))
                .thenReturn(advertiser);

        Advertisement advertisement = mock(Advertisement.class);

        when(advertisement.getAdvertisementId()).thenReturn(10L);
        when(advertisement.getAdvertiser()).thenReturn(advertiser);
        when(advertisement.getAdvertisementName()).thenReturn("A 광고");
        when(advertisement.getBudgetAmount()).thenReturn(BigDecimal.valueOf(1_000_000));
        when(advertisement.getImpressionPrice()).thenReturn(BigDecimal.valueOf(1_000));
        when(advertisement.getConversionPrice()).thenReturn(BigDecimal.valueOf(5_000));
        when(advertisement.getStatus()).thenReturn(AdvertisementStatus.ACTIVE);

        when(advertisementReader.getAdvertisementList(1L))
                .thenReturn(List.of(advertisement));

        // when
        AdvertiserDetailResponse response =
                advertiserService.getAdvertiserWithAdvertisementList(1L);

        // then
        assertEquals(1L, response.advertiserId());
        assertEquals("A", response.advertiserName());

        assertEquals(1, response.advertisements().size());

        AdvertisementResponse advertisementResponse = response.advertisements().get(0);

        assertEquals(10L, advertisementResponse.advertisementId());
        assertEquals("A 광고", advertisementResponse.advertisementName());
        assertEquals(BigDecimal.valueOf(1_000_000), advertisementResponse.budgetAmount());
        assertEquals("ACTIVE", advertisementResponse.status());

        verify(advertiserReader).getById(1L);
        verify(advertisementReader).getAdvertisementList(1L);
    }

    @Test
    @DisplayName("등록된 광고가 없는 광고주는 빈 광고 목록 반환")
    void getAdvertiserWithEmptyAdvertisementList() {
        // given
        Advertiser advertiser = mock(Advertiser.class);

        when(advertiser.getAdvertiserId()).thenReturn(1L);
        when(advertiser.getAdvertiserName()).thenReturn("신규 광고주");

        when(advertiserReader.getById(1L))
                .thenReturn(advertiser);

        when(advertisementReader.getAdvertisementList(1L))
                .thenReturn(List.of());

        // when
        AdvertiserDetailResponse response =
                advertiserService.getAdvertiserWithAdvertisementList(1L);

        // then
        assertEquals(1L, response.advertiserId());
        assertEquals("신규 광고주", response.advertiserName());
        assertTrue(response.advertisements().isEmpty());

        verify(advertisementReader).getAdvertisementList(1L);
    }
}