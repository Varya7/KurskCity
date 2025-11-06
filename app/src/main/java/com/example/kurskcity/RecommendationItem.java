package com.example.kurskcity;

public class RecommendationItem {
    private Attraction attraction;
    private KurskEventsParser.Event event;

    public RecommendationItem(Attraction attraction) {
        this.attraction = attraction;
        this.event = null;
    }

    public RecommendationItem(KurskEventsParser.Event event) {
        this.event = event;
        this.attraction = null;
    }

    public boolean isAttraction() {
        return attraction != null;
    }

    public boolean isEvent() {
        return event != null;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public KurskEventsParser.Event getEvent() {
        return event;
    }
}