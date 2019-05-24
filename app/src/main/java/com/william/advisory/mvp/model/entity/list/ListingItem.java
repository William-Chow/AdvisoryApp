package com.william.advisory.mvp.model.entity.list;

/**
 * Created by William Chow on 2019-05-23.
 */
public class ListingItem {

    private int id;
    private String list_name;
    private String distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
