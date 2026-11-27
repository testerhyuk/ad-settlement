package io.github.hyuk.adsettlement.advertising.presentation;

import io.github.hyuk.adsettlement.advertising.business.AdvertisementService;
import io.github.hyuk.adsettlement.advertising.exception.DuplicateAdvertisementNameException;
import io.github.hyuk.adsettlement.advertising.persistence.enums.AdvertisementStatus;
import io.github.hyuk.adsettlement.advertising.presentation.advice.AdvertisementExceptionHandler;
import io.github.hyuk.adsettlement.advertising.presentation.advice.AdvertiserExceptionHandler;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertisementResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({
        AdvertisementController.class,
        AdvertisementExceptionHandler.class
})
class AdvertisementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvertisementService advertisementService;

    @Test
    @DisplayName("광고 등록 성공 시 201 Created 반환")
    void register() throws Exception {
        // given
        AdvertisementResponse response = new AdvertisementResponse(
                1L,
                1L,
                "V",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10_000),
                AdvertisementStatus.ACTIVE.toString(),
                Instant.parse("2026-11-28T09:00:00Z"),
                Instant.parse("2026-11-29T09:00:00Z"),
                Instant.parse("2026-12-29T09:00:00Z")
        );

        when(advertisementService.register(any(AdvertisementRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "advertiserId": 1,
                                    "advertisementName": "V",
                                    "budgetAmount": 1000000,
                                    "impressionPrice": 100,
                                    "conversionPrice": 10000,
                                    "status": "ACTIVE",
                                    "startedAt": "2026-11-29T09:00:00Z",
                                    "expiredAt": "2026-12-29T09:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.advertisementId").value(1))
                .andExpect(jsonPath("$.advertiserId").value(1))
                .andExpect(jsonPath("$.advertisementName").value("V"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.expiredAt").exists());

        verify(advertisementService).register(any(AdvertisementRequest.class));
    }

    @Test
    @DisplayName("잘못된 광고 상태값으로 요청하면 400 Bad Request 반환")
    void registerWithInvalidStatus() throws Exception {
        // given
        String request = """
                {
                    "advertiserId": 1,
                    "advertisementName": "V",
                    "budgetAmount": 1000000,
                    "impressionPrice": 100,
                    "conversionPrice": 10000,
                    "status": "INVALID",
                    "startedAt": "2026-11-29T09:00:00Z",
                    "expiredAt": "2026-12-29T09:00:00Z"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(advertisementService, never())
                .register(any(AdvertisementRequest.class));
    }

    @Test
    @DisplayName("중복된 광고 이름으로 등록하면 409 Conflict 반환")
    void registerWithDuplicateAdvertisementName() throws Exception {
        // given
        String request = """
                {
                    "advertiserId": 1,
                    "advertisementName": "V",
                    "budgetAmount": 1000000,
                    "impressionPrice": 100,
                    "conversionPrice": 10000,
                    "status": "ACTIVE",
                    "startedAt": "2026-11-29T09:00:00Z",
                    "expiredAt": "2026-12-29T09:00:00Z"
                }
                """;

        when(advertisementService.register(any(AdvertisementRequest.class)))
                .thenThrow(new DuplicateAdvertisementNameException("이미 등록된 광고입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 등록된 광고입니다."));

        verify(advertisementService).register(any(AdvertisementRequest.class));
    }
}