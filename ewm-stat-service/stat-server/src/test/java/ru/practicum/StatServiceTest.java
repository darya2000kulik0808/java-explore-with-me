package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatServiceImpl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatServiceTest {

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatServiceImpl service;

    private static final LocalDateTime NOW = LocalDateTime.now();

    private ViewStatsDto.ViewStatsDtoBuilder viewStatsDtoBuilder1;
    private ViewStatsDto.ViewStatsDtoBuilder viewStatsDtoBuilder2;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String start;
    private String end;

    @BeforeEach
    void setup() {
        String app1 = "ewm-main-service";
        String uri1 = "/events/1";
        String ip1 = "121.0.0.1";

        String app2 = "ewm-main-service";
        String uri2 = "/events/3";
        String ip2 = "121.0.0.1";

        EndpointHitDto.EndpointHitDtoBuilder hitDtoBuilder = EndpointHitDto.builder()
                .app(app1)
                .uri(uri1)
                .ip(ip1)
                .timestamp(NOW);

        viewStatsDtoBuilder1 = ViewStatsDto.builder()
                .app(app1)
                .uri(uri1);

        viewStatsDtoBuilder2 = ViewStatsDto.builder()
                .app(app2)
                .uri(uri2);

        start = URLEncoder.encode(NOW.minusHours(1).format(formatter), StandardCharsets.UTF_8);
        end = URLEncoder.encode(NOW.format(formatter), StandardCharsets.UTF_8);
    }

    @Test
    void shouldGetStatsForEmptyListOfUris() {
        //Not Unique  | Empty List
        when(statsRepository.findAllByTimestampBetween(any(), any())).thenReturn(Collections.emptyList());
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, Collections.emptyList(), false);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void shouldGetStatsForEmptyListOfUrisAndUnique() {
        // Unique  | Empty List
        when(statsRepository.findAllByTimestampBetweenWithUniqueIp(any(), any())).thenReturn(Collections.emptyList());
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, Collections.emptyList(), true);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void shouldGetStatsForListOfUris() {
        // Not Unique  | Single List
        ViewStatsDto viewStatsDto = viewStatsDtoBuilder1.hits(1L).build();

        List<String> uris = List.of("/events/1");

        when(statsRepository.findAllByTimestampBetweenAndUriIn(any(), any(), any())).thenReturn(List.of(viewStatsDto));
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, uris, false);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void shouldGetStatsForListOfUrisAndUniqueIp() {
        // Not Unique  | Single List
        ViewStatsDto viewStatsDto = viewStatsDtoBuilder1.hits(1L).build();

        List<String> uris = List.of("/events/1");

        when(statsRepository.findAllByTimestampBetweenAndUriInAndIpUnique(any(), any(), any())).thenReturn(List.of(viewStatsDto));
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, uris, true);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(1);
    }


    @Test
    void shouldGetStatsForMultipleListOfUris() {
        // Not Unique  | Multiple List
        ViewStatsDto viewStatsDto1 = viewStatsDtoBuilder1.hits(1L).build();
        ViewStatsDto viewStatsDto2 = viewStatsDtoBuilder2.hits(3L).build();
        List<String> uris = List.of("/events/1", "/events/3");
        when(statsRepository.findAllByTimestampBetweenAndUriIn(any(), any(), any()))
                .thenReturn(List.of(viewStatsDto1, viewStatsDto2));
        List<ViewStatsDto> viewStatsDTOs = service.getStats(start, end, uris, false);
        assertThat(viewStatsDTOs)
                .isNotNull()
                .hasSize(2)
                .isEqualTo(List.of(viewStatsDto2, viewStatsDto1));
    }
}