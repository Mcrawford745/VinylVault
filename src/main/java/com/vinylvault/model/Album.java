package com.vinylvault.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {

    private String id = UUID.randomUUID().toString();
    private String title = "";
    private String artist = "";
    private String format = "Vinyl LP";   // "Vinyl LP", "Vinyl 7\"", "CD", "Cassette"
    private String genre = "";
    private String mood = "";
    private int releaseYear;
    private String label = "";
    private String catalogNumber = "";
    private String barcode = "";
    private String mediaCondition = "NM"; // M, NM, VG+, VG, G+, F, P
    private String sleeveCondition = "NM";
    private List<String> tracklist = new ArrayList<>();
    private String notes = "";
    private boolean wishlist = false;
    private double purchasePrice;
    private String dateAdded = java.time.Instant.now().toString();
    private int playCount;

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getCatalogNumber() { return catalogNumber; }
    public void setCatalogNumber(String catalogNumber) { this.catalogNumber = catalogNumber; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getMediaCondition() { return mediaCondition; }
    public void setMediaCondition(String mediaCondition) { this.mediaCondition = mediaCondition; }

    public String getSleeveCondition() { return sleeveCondition; }
    public void setSleeveCondition(String sleeveCondition) { this.sleeveCondition = sleeveCondition; }

    public List<String> getTracklist() { return tracklist; }
    public void setTracklist(List<String> tracklist) { this.tracklist = tracklist; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isWishlist() { return wishlist; }
    public void setWishlist(boolean wishlist) { this.wishlist = wishlist; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public int getPlayCount() { return playCount; }
    public void setPlayCount(int playCount) { this.playCount = playCount; }

    @Override
    public String toString() {
        return artist + " – " + title + " (" + format + ", " + releaseYear + ")";
    }
}
