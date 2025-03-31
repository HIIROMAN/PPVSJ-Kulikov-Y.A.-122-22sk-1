package com.example.dniproapartmentfinder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String url;
    private Double priceUah; // Має бути Double, а не double
    private Double priceUsd; // Має бути Double, а не double
    private String datePosted;

    // Геттери і сеттери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Double getPriceUah() { return priceUah; }
    public void setPriceUah(Double priceUah) { this.priceUah = priceUah; }
    public Double getPriceUsd() { return priceUsd; }
    public void setPriceUsd(Double priceUsd) { this.priceUsd = priceUsd; }
    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }
}