package ru.practicum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;

    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }

    public ViewStatsDto() {
    }

    public ViewStatsDto(String app) {
        this.app = app;
    }

    public ViewStatsDto(Long hits) {
        this.hits = hits;
    }
}
