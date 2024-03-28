package me.dio.sdw24.domain.exceptions;

public class ChampionNotFoundException extends RuntimeException {

    private Long championId;

    public ChampionNotFoundException(Long championId) {
        super("Champion %d not found.".formatted(championId));
    }
}
