package com.ahmetc.subconsciousmanager.Models;

public class Affirmations {
    private int affirmation_id;
    private String affirmation_text;
    private Categories category;
    private int isFav;

    public Affirmations() {}
    public Affirmations(int affirmation_id, String affirmation_text, Categories categories, int isFav) {
        this.affirmation_id = affirmation_id;
        this.affirmation_text = affirmation_text;
        this.category = categories;
        this.isFav = isFav;
    }
    public int getAffirmation_id() {
        return affirmation_id;
    }

    public void setAffirmation_id(int affirmation_id) {
        this.affirmation_id = affirmation_id;
    }

    public String getAffirmation_text() {
        return affirmation_text;
    }

    public void setAffirmation_text(String affirmation_text) {
        this.affirmation_text = affirmation_text;
    }

    public Categories getCategories() {
        return category;
    }

    public void setCategories(Categories category) {
        this.category = category;
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }
}
