package com.ahmetc.subconsciousmanager.Models;

public class Categories {
    private int category_id;
    private String category_name;
    private String icon_path;

    public Categories(int category_id, String category_name, String icon_path) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.icon_path = icon_path;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(String icon_path) {
        this.icon_path = icon_path;
    }
}