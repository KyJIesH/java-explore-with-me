package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.ResponseStatsDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;

    private final StatsDto statsDto = new StatsDto(1L, "ewm-main-service", "/events/1", "198.168.0.0",
            LocalDateTime.of(2024, 7, 29, 22, 30, 0));

    private final ResponseStatsDto responseStatsDto = new ResponseStatsDto("app-main-service", "/events/1", 2L);

    private final List<ResponseStatsDto> responseStatsDtos = Collections.singletonList(responseStatsDto);

    @Test
    void createStatTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hit")
                        .content(objectMapper.writeValueAsString(statsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void getStatsTest() throws Exception {
        when(statsService.getStats(any())).thenReturn(responseStatsDtos);

        mockMvc.perform(get("/stats?start=2020-05-05 00:00:00&end=2035-05-05 00:00:00&unique=false")
                        .content(objectMapper.writeValueAsString(responseStatsDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getStatsTestIncorrectDateNull() throws Exception {
        when(statsService.getStats(any())).thenReturn(responseStatsDtos);

        mockMvc.perform(get("/stats?start=null&end=null&unique=false")
                        .content(objectMapper.writeValueAsString(responseStatsDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatsTestIncorrectDate() throws Exception {
        when(statsService.getStats(any())).thenReturn(responseStatsDtos);

        mockMvc.perform(get("/stats?start=0000-00-00 00:00:00&end=0000-00-00 00:00:00&unique=false")
                        .content(objectMapper.writeValueAsString(responseStatsDto)))
                .andExpect(status().isBadRequest());
    }
}