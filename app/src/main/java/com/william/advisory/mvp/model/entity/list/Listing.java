package com.william.advisory.mvp.model.entity.list;

import com.google.gson.annotations.SerializedName;
import com.william.advisory.mvp.model.entity.Status;

import java.util.ArrayList;

/**
 * Created by William Chow on 2019-05-23.
 */
public class Listing {
    @SerializedName(value = "listing")
    private ArrayList<ListingItem> listingItem;
    private Status status;

    public ArrayList<ListingItem> getListingItem() {
        return listingItem;
    }

    public void setListingItem(ArrayList<ListingItem> listingItem) {
        this.listingItem = listingItem;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
