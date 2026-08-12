package com.vinylvault.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vinylvault.model.Album;
import com.vinylvault.model.DatabaseWrapper;
import com.vinylvault.model.ListeningLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageService {

    private static final String DB_FILENAME = "vinylvault_db.json";

    private final ObjectMapper mapper;
    private final Path dbPath;
    private DatabaseWrapper db;

    public StorageService() {
        mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        // Store db file next to the jar / in the working directory
        dbPath = Paths.get(System.getProperty("user.home"), ".vinylvault", DB_FILENAME);
        dbPath.getParent().toFile().mkdirs();
        load();
    }

    // -----------------------------------------------------------------------
    // Load / Save
    // -----------------------------------------------------------------------

    private void load() {
        File file = dbPath.toFile();
        if (file.exists()) {
            try {
                db = mapper.readValue(file, DatabaseWrapper.class);
            } catch (IOException e) {
                System.err.println("Failed to load DB, starting fresh: " + e.getMessage());
                db = new DatabaseWrapper();
            }
        } else {
            db = new DatabaseWrapper();
        }
    }

    public void save() {
        try {
            mapper.writeValue(dbPath.toFile(), db);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save database: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Collection CRUD
    // -----------------------------------------------------------------------

    public DatabaseWrapper getDb() { return db; }

    public void addAlbum(Album album) {
        if (album.isWishlist()) {
            db.getWishlist().add(album);
        } else {
            db.getCollection().add(album);
        }
        save();
    }

    public void updateAlbum(Album updated) {
        replaceInList(db.getCollection(), updated);
        replaceInList(db.getWishlist(), updated);
        save();
    }

    public void deleteAlbum(String id) {
        db.getCollection().removeIf(a -> a.getId().equals(id));
        db.getWishlist().removeIf(a -> a.getId().equals(id));
        save();
    }

    /** Move a wishlist item to the owned collection. */
    public void moveToCollection(String albumId) {
        db.getWishlist().stream()
                .filter(a -> a.getId().equals(albumId))
                .findFirst()
                .ifPresent(album -> {
                    db.getWishlist().remove(album);
                    album.setWishlist(false);
                    db.getCollection().add(album);
                    save();
                });
    }

    // -----------------------------------------------------------------------
    // Listening Log
    // -----------------------------------------------------------------------

    /**
     * Records a listen: creates a ListeningLog entry, increments playCount on
     * the target album, and persists everything.
     */
    public void logListen(String albumId, String notes) {
        Album album = db.getCollection().stream()
                .filter(a -> a.getId().equals(albumId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Album not found: " + albumId));

        album.setPlayCount(album.getPlayCount() + 1);

        ListeningLog entry = new ListeningLog(
                album.getId(),
                album.getTitle(),
                album.getArtist(),
                album.getFormat(),
                notes
        );
        db.getListeningHistory().add(0, entry); // newest first
        save();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void replaceInList(java.util.List<Album> list, Album updated) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(updated.getId())) {
                list.set(i, updated);
                return;
            }
        }
    }
}
