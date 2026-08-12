package com.vinylvault.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListeningLog {

    private String id = UUID.randomUUID().toString();
    private String albumId = "";
    private String albumTitle = "";
    private String artist = "";
    private String format = "";
    private String listenedAt = java.time.Instant.now().toString();
    private String notes = "";

    public ListeningLog() {}

    public ListeningLog(String albumId, String albumTitle, String artist, String format, String notes) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.artist = artist;
        this.format = format;
        this.notes = notes;
        this.listenedAt = java.time.Instant.now().toString();
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAlbumId() { return albumId; }
    public void setAlbumId(String albumId) { this.albumId = albumId; }

    public String getAlbumTitle() { return albumTitle; }
    public void setAlbumTitle(String albumTitle) { this.albumTitle = albumTitle; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getListenedAt() { return listenedAt; }
    public void setListenedAt(String listenedAt) { this.listenedAt = listenedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
