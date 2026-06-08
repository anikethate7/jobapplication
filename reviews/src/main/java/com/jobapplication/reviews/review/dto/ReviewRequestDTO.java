package com.jobapplication.reviews.review.dto;

public class ReviewRequestDTO {
    private String title;
    private String description;
    private double rating;

    public ReviewRequestDTO() {}

    public ReviewRequestDTO(String title, String description, double rating) {
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}