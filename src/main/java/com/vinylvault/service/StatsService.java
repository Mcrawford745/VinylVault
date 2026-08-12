package com.vinylvault.service;

import com.vinylvault.model.Album;
import com.vinylvault.model.CollectionStats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsService {

    private final StorageService storage;

    public StatsService(StorageService storage) {
        this.storage = storage;
    }

    public CollectionStats compute() {
        List<Album> col = storage.getDb().getCollection();

        int totalOwned   = col.size();
        int totalWishlist = storage.getDb().getWishlist().size();
        int totalListens  = storage.getDb().getListeningHistory().size();
        double totalSpent = col.stream().mapToDouble(Album::getPurchasePrice).sum();

        Map<String, Long> byFormat = col.stream()
                .collect(Collectors.groupingBy(Album::getFormat, Collectors.counting()));

        Map<String, Long> byDecade = col.stream()
                .filter(a -> a.getReleaseYear() > 0)
                .collect(Collectors.groupingBy(
                        a -> (a.getReleaseYear() / 10 * 10) + "s",
                        Collectors.counting()
                ));

        // Top 10 artists by item count
        Map<String, Long> topArtists = col.stream()
                .collect(Collectors.groupingBy(Album::getArtist, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return new CollectionStats(totalOwned, totalWishlist, totalListens, totalSpent,
                byFormat, byDecade, topArtists);
    }
}
