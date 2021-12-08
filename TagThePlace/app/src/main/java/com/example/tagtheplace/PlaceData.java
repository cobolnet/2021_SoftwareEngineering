package com.example.tagtheplace;

public class PlaceData {

    public int id;
    public String name;
    public String tag;
    public int like;
    public int dislike;
    public float lat;
    public float lng;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setDislike(int disLike) {
        this.dislike = disLike;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
