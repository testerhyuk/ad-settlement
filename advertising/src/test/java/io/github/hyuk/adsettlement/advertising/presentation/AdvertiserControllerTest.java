package io.github.hyuk.adsettlement.advertising.presentation;

import io.github.hyuk.adsettlement.advertising.business.AdvertiserService;
import io.github.hyuk.adsettlement.advertising.exception.AdvertiserNotFoundException;
import io.github.hyuk.adsettlement.advertising.exception.DuplicateBusinessRegistrationNumberException;
import io.github.hyuk.adsettlement.advertising.presentation.advice.AdvertiserExceptionHandler;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementResponse;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserDetailResponse;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
        AdvertiserController.class,
        AdvertiserExceptionHandler.class
})
class AdvertiserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvertiserService advertiserService;

    @Test
    @DisplayName("광고주 등록 성공 시 201 Created 반환")
    void register() throws Exception {
        // given
        AdvertiserRequest request =
                new AdvertiserRequest("A", "1234567890");

        AdvertiserResponse response = new AdvertiserResponse(
                1L,
                "A",
                "1234567890",
                Instant.parse("2026-11-27T09:00:00Z")
        );

        when(advertiserService.register(any(AdvertiserRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "advertiserName": "A",
                                "businessRegistrationNumber": "1234567890"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.advertiserId").value(1))
                .andExpect(jsonPath("$.advertiserName").value("A"))
                .andExpect(jsonPath("$.businessRegistrationNumber").value("1234567890"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(advertiserService).register(any(AdvertiserRequest.class));
    }

    @Test
    @DisplayName("광고주 이름이 비어 있으면 400 Bad Request 반환")
    void registerWithBlankAdvertiserName() throws Exception {
        // given
        String request = """
            {
                "advertiserName": "",
                "businessRegistrationNumber": "1234567890"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(advertiserService, never())
                .register(any(AdvertiserRequest.class));
    }

    @Test
    @DisplayName("중복된 사업자등록번호로 요청하면 409 Conflict 반환")
    void registerWithDuplicateBusinessRegistrationNumber() throws Exception {
        // given
        String request = """
            {
                "advertiserName": "A",
                "businessRegistrationNumber": "1234567890"
            }
            """;

        when(advertiserService.register(any(AdvertiserRequest.class)))
                .thenThrow(new DuplicateBusinessRegistrationNumberException(
                        "이미 등록된 사업자등록번호입니다."
                ));

        // when & then
        mockMvc.perform(post("/api/v1/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("이미 등록된 사업자등록번호입니다."));

        verify(advertiserService).register(any(AdvertiserRequest.class));
    }

    @Test
    @DisplayName("광고주 단건 조회 성공")
    void getAdvertiser() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdvertiserResponse response = new AdvertiserResponse(
                1L,
                "A",
                "1234567890",
                createdAt
        );

        when(advertiserService.getAdvertiser(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/advertisers/{advertiserId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.advertiserId").value(1))
                .andExpect(jsonPath("$.advertiserName").value("A"))
                .andExpect(jsonPath("$.businessRegistrationNumber").value("1234567890"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(advertiserService).getAdvertiser(1L);
    }

    @Test
    @DisplayName("존재하지 않는 광고주 조회 시 404 반환")
    void getAdvertiserNotFound() throws Exception {
        // given
        when(advertiserService.getAdvertiser(999L))
                .thenThrow(new AdvertiserNotFoundException("존재하지 않는 광고주입니다."));

        // when & then
        mockMvc.perform(get("/api/v1/advertisers/{advertiserId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 광고주입니다."));

        verify(advertiserService).getAdvertiser(999L);
    }

    @Test
    @DisplayName("광고주와 소속 광고 목록 조회 성공")
    void getAdvertiserWithAdvertisementList() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdvertisementResponse advertisement = new AdvertisementResponse(
                10L,
                1L,
                "A 광고",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(1_000),
                BigDecimal.valueOf(5_000),
                "ACTIVE",
                createdAt,
                Instant.parse("2026-12-01T00:00:00Z"),
                Instant.parse("2026-12-31T23:59:59Z")
        );

        AdvertiserDetailResponse response = new AdvertiserDetailResponse(
                1L,
                "A",
                "1234567890",
                createdAt,
                List.of(advertisement)
        );

        when(advertiserService.getAdvertiserWithAdvertisementList(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/advertisers/{advertiserId}/advertisements", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.advertiserId").value(1))
                .andExpect(jsonPath("$.advertiserName").value("A"))
                .andExpect(jsonPath("$.advertisements.length()").value(1))
                .andExpect(jsonPath("$.advertisements[0].advertisementId").value(10))
                .andExpect(jsonPath("$.advertisements[0].advertisementName").value("A 광고"))
                .andExpect(jsonPath("$.advertisements[0].status").value("ACTIVE"));

        verify(advertiserService).getAdvertiserWithAdvertisementList(1L);
    }

    @Test
    @DisplayName("등록된 광고가 없는 광고주는 빈 광고 목록 반환")
    void getAdvertiserWithEmptyAdvertisementList() throws Exception {
        // given
        Instant createdAt = Instant.parse("2026-11-28T09:00:00Z");

        AdvertiserDetailResponse response = new AdvertiserDetailResponse(
                1L,
                "신규 광고주",
                "1234567890",
                createdAt,
                List.of()
        );

        when(advertiserService.getAdvertiserWithAdvertisementList(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/advertisers/{advertiserId}/advertisements", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.advertiserId").value(1))
                .andExpect(jsonPath("$.advertiserName").value("신규 광고주"))
                .andExpect(jsonPath("$.advertisements").isEmpty());

        verify(advertiserService).getAdvertiserWithAdvertisementList(1L);
    }
}