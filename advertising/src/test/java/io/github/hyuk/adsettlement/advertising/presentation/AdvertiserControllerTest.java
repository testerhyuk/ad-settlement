package io.github.hyuk.adsettlement.advertising.presentation;

import io.github.hyuk.adsettlement.advertising.business.AdvertiserService;
import io.github.hyuk.adsettlement.advertising.exception.DuplicateBusinessRegistrationNumberException;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserRequest;
import io.github.hyuk.adsettlement.advertising.presentation.dto.AdvertiserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdvertiserController.class)
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
}