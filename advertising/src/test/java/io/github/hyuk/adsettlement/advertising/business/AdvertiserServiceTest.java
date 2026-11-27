package io.github.hyuk.adsettlement.advertising.business;

import io.github.hyuk.adsettlement.advertising.exception.DuplicateBusinessRegistrationNumberException;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserAppender;
import io.github.hyuk.adsettlement.advertising.implement.AdvertiserReader;
import io.github.hyuk.adsettlement.advertising.persistence.Advertiser;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertiserServiceTest {
    @Mock
    private AdvertiserReader advertiserReader;

    @Mock
    private AdvertiserAppender advertiserAppender;

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
}