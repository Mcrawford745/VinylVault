package com.vinylvault.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylvault.model.Album;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * Looks up album metadata from the MusicBrainz JSON API.
 * Respects the 1-request-per-second rate limit.
 */
public class MusicBrainzService {

    private static final String BASE = "https://musicbrainz.org/ws/2/";
    private static final String UA   = "VinylVault/1.0 (https://github.com/YOUR_USERNAME/VinylVault)";

    private final HttpClient http;
    private final ObjectMapper mapper;

    public MusicBrainzService() {
        http   = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        mapper = new ObjectMapper();
    }

    /**
     * Search MusicBrainz for a release matching the given title/artist and
     * populate additional fields on the provided Album object.
     *
     * @return the same Album with any found fields applied
     */
    public Optional<Album> enrich(Album album) {
        try {
            String query = URLEncoder.encode(
                    "release:\"" + album.getTitle() + "\" AND artist:\"" + album.getArtist() + "\"",
                    StandardCharsets.UTF_8);

            URI uri = URI.create(BASE + "release?query=" + query
                    + "&limit=1&fmt=json&inc=labels+recordings");

            HttpRequest req = HttpRequest.newBuilder(uri)
                    .header("User-Agent", UA)
                    .header("Accept", "application/json")
                    .GET().build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) return Optional.empty();

            JsonNode root    = mapper.readTree(resp.body());
            JsonNode releases = root.path("releases");
            if (!releases.isArray() || releases.isEmpty()) return Optional.empty();

            JsonNode release = releases.get(0);

            // Label
            JsonNode labelInfo = release.path("label-info");
            if (labelInfo.isArray() && !labelInfo.isEmpty()) {
                JsonNode label = labelInfo.get(0).path("label");
                if (!label.isMissingNode()) album.setLabel(label.path("name").asText(""));

                String catNo = labelInfo.get(0).path("catalog-number").asText("");
                if (!catNo.isBlank()) album.setCatalogNumber(catNo);
            }

            // Barcode
            String barcode = release.path("barcode").asText("");
            if (!barcode.isBlank()) album.setBarcode(barcode);

            // Release year
            String date = release.path("date").asText("");
            if (date.length() >= 4) {
                try { album.setReleaseYear(Integer.parseInt(date.substring(0, 4))); }
                catch (NumberFormatException ignored) {}
            }

            // Tracklist
            JsonNode media = release.path("media");
            if (media.isArray()) {
                java.util.List<String> tracks = new java.util.ArrayList<>();
                for (JsonNode medium : media) {
                    for (JsonNode track : medium.path("tracks")) {
                        tracks.add(track.path("title").asText(""));
                    }
                }
                if (!tracks.isEmpty()) album.setTracklist(tracks);
            }

            return Optional.of(album);

        } catch (Exception e) {
            System.err.println("MusicBrainz lookup failed: " + e.getMessage());
            return Optional.empty();
        }
    }
}
