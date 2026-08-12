package com.vinylvault.service;

import com.vinylvault.model.Album;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * "I'm Feeling Lucky" recommendation engine.
 */
public class LuckyService {

    public enum Mode { RANDOM, FORMAT, MOOD, FORGOTTEN }

    private final StorageService storage;
    private final Random rng = new Random();

    public LuckyService(StorageService storage) {
        this.storage = storage;
    }

    /** Completely random pick from the owned collection. */
    public Optional<Album> pickRandom() {
        List<Album> col = storage.getDb().getCollection();
        if (col.isEmpty()) return Optional.empty();
        return Optional.of(col.get(rng.nextInt(col.size())));
    }

    /** Filter by format, then pick at random. Pass null to skip filter. */
    public Optional<Album> pickByFormat(String format) {
        List<Album> pool = storage.getDb().getCollection().stream()
                .filter(a -> format == null || "ALL".equals(format) || a.getFormat().equals(format))
                .toList();
        return pickFrom(pool);
    }

    /** Filter by mood, then pick at random. */
    public Optional<Album> pickByMood(String mood) {
        List<Album> pool = storage.getDb().getCollection().stream()
                .filter(a -> mood == null || a.getMood().equalsIgnoreCase(mood))
                .toList();
        return pickFrom(pool);
    }

    /**
     * "Forgotten Favorites" — returns the album(s) with the lowest playCount,
     * then picks randomly among ties.
     */
    public Optional<Album> pickForgotten() {
        List<Album> col = storage.getDb().getCollection();
        if (col.isEmpty()) return Optional.empty();
        int min = col.stream().mapToInt(Album::getPlayCount).min().orElse(0);
        List<Album> pool = col.stream().filter(a -> a.getPlayCount() == min).toList();
        return pickFrom(pool);
    }

    // -----------------------------------------------------------------------

    private Optional<Album> pickFrom(List<Album> pool) {
        if (pool.isEmpty()) return Optional.empty();
        return Optional.of(pool.get(rng.nextInt(pool.size())));
    }
}
