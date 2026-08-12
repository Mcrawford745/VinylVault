package com.vinylvault.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseWrapper {

    private List<Album> collection = new ArrayList<>();
    private List<Album> wishlist = new ArrayList<>();
    private List<ListeningLog> listeningHistory = new ArrayList<>();

    public List<Album> getCollection() { return collection; }
    public void setCollection(List<Album> collection) { this.collection = collection; }

    public List<Album> getWishlist() { return wishlist; }
    public void setWishlist(List<Album> wishlist) { this.wishlist = wishlist; }

    public List<ListeningLog> getListeningHistory() { return listeningHistory; }
    public void setListeningHistory(List<ListeningLog> listeningHistory) { this.listeningHistory = listeningHistory; }
}
