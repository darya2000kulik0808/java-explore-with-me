package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class StatRepositoryTest {
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private TestEntityManager em;

    @Autowired
    private StatsRepository statsRepository;

    private EndpointHit hit;
    private EndpointHit.EndpointHitBuilder hitBuilder;
    private LocalDateTime start;
    private LocalDateTime end;
    private ViewStatsDto viewStatsDto1;
    private ViewStatsDto viewStatsDto2;

    @BeforeEach
    void setup() {
        String appName = "ewm-main-service";
        String uri1 = "/events/1";
        String ip1 = "121.0.0.1";

        String uri2 = "/events/2";
        String ip2 = "121.0.0.2";

        viewStatsDto1 = new ViewStatsDto();
        viewStatsDto1.setApp(appName);
        viewStatsDto1.setUri(uri1);

        viewStatsDto2 = new ViewStatsDto();
        viewStatsDto2.setApp(appName);
        viewStatsDto2.setUri(uri2);

        hitBuilder = EndpointHit.builder()
                .app(appName)
                .uri(uri1)
                .ip(ip1)
                .timestamp(NOW);

        EndpointHit hit1 = new EndpointHit();
        hit1.setApp(appName);
        hit1.setUri(uri1);
        hit1.setIp(ip1);
        hit1.setTimestamp(NOW.minusMinutes(10));

        statsRepository.save(hit1);

        EndpointHit hit2 = new EndpointHit();
        hit2.setApp(appName);
        hit2.setUri(uri2);
        hit2.setIp(ip1);
        hit2.setTimestamp(NOW.minusMinutes(9));

        statsRepository.save(hit2);

        EndpointHit hit3 = new EndpointHit();
        hit3.setApp(appName);
        hit3.setUri(uri1);
        hit3.setIp(ip2);
        hit3.setTimestamp(NOW.minusMinutes(8));

        statsRepository.save(hit3);

        EndpointHit hit4 = new EndpointHit();
        hit4.setApp(appName);
        hit4.setUri(uri1);
        hit4.setIp(ip2);
        hit4.setTimestamp(NOW.minusMinutes(7));

        statsRepository.save(hit4);

        start = NOW.minusHours(2);
        end = NOW;
    }

    @Test
    public void testContextLoads() {
        assertNotNull(em);
    }

    @Test
    void addEndpointHit() {
        hit = hitBuilder.build();
        EndpointHit hitAdded = statsRepository.save(hit);

        assertThat(hitAdded)
                .isNotNull()
                .hasFieldOrPropertyWithValue("app", hitAdded.getApp());
    }

    @Test
    void shouldReturnAllByTimestampBetween() {
        viewStatsDto1.setHits(3L);
        viewStatsDto2.setHits(1L);

        List<ViewStatsDto> viewStatsDtoReturned = statsRepository.findAllByTimestampBetween(start, end);
        assertThat(viewStatsDtoReturned)
                .isNotNull()
                .hasOnlyElementsOfType(ViewStatsDto.class);
        assertEquals(viewStatsDtoReturned.get(0).getHits(), viewStatsDto1.getHits());
        assertEquals(viewStatsDtoReturned.get(0).getApp(), viewStatsDto1.getApp());
        assertEquals(viewStatsDtoReturned.get(0).getUri(), viewStatsDto1.getUri());

        assertEquals(viewStatsDtoReturned.get(1).getHits(), viewStatsDto2.getHits());
        assertEquals(viewStatsDtoReturned.get(1).getApp(), viewStatsDto2.getApp());
        assertEquals(viewStatsDtoReturned.get(1).getUri(), viewStatsDto2.getUri());
    }

    @Test
    void shouldReturnAllByTimestampBetweenUniqueIp() {
        viewStatsDto1.setHits(2L);
        viewStatsDto2.setHits(1L);

        List<ViewStatsDto> viewStatsDtoReturned = statsRepository.findAllByTimestampBetweenWithUniqueIp(start, end);
        assertThat(viewStatsDtoReturned)
                .isNotNull()
                .hasOnlyElementsOfType(ViewStatsDto.class);
        assertEquals(viewStatsDtoReturned.get(0).getHits(), viewStatsDto1.getHits());
        assertEquals(viewStatsDtoReturned.get(0).getApp(), viewStatsDto1.getApp());
        assertEquals(viewStatsDtoReturned.get(0).getUri(), viewStatsDto1.getUri());

        assertEquals(viewStatsDtoReturned.get(1).getHits(), viewStatsDto2.getHits());
        assertEquals(viewStatsDtoReturned.get(1).getApp(), viewStatsDto2.getApp());
        assertEquals(viewStatsDtoReturned.get(1).getUri(), viewStatsDto2.getUri());
    }

    @Test
    void shouldReturnAllByTimestampBetweenAndUriIn() {
        viewStatsDto1.setHits(3L);

        List<String> uris = List.of("/events/1");

        List<ViewStatsDto> viewStatsDtoReturned = statsRepository.findAllByTimestampBetweenAndUriIn(start, end, uris);
        assertThat(viewStatsDtoReturned)
                .isNotNull()
                .hasOnlyElementsOfType(ViewStatsDto.class);
        assertEquals(viewStatsDtoReturned.get(0).getHits(), viewStatsDto1.getHits());
        assertEquals(viewStatsDtoReturned.get(0).getApp(), viewStatsDto1.getApp());
        assertEquals(viewStatsDtoReturned.get(0).getUri(), viewStatsDto1.getUri());
    }

    @Test
    void shouldReturnAllByTimestampBetweenAndUriInUniqueIp() {
        viewStatsDto1.setHits(2L);

        List<String> uris = List.of("/events/1");

        List<ViewStatsDto> viewStatsDtoReturned = statsRepository.findAllByTimestampBetweenAndUriInAndIpUnique(start, end, uris);
        assertThat(viewStatsDtoReturned)
                .isNotNull()
                .hasOnlyElementsOfType(ViewStatsDto.class);
        assertEquals(viewStatsDtoReturned.get(0).getHits(), viewStatsDto1.getHits());
        assertEquals(viewStatsDtoReturned.get(0).getApp(), viewStatsDto1.getApp());
        assertEquals(viewStatsDtoReturned.get(0).getUri(), viewStatsDto1.getUri());
    }
}
