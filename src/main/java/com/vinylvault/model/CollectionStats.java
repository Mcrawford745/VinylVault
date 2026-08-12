package com.vinylvault.model;

import java.util.Map;

public record CollectionStats(
        int totalOwned,
        int totalWishlist,
        int totalListens,
        double totalSpent,
        Map<String, Long> byFormat,
        Map<String, Long> byDecade,
        Map<String, Long> topArtists
) {}
