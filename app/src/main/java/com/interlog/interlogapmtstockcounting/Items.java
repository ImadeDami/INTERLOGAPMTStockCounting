package com.interlog.interlogapmtstockcounting;

public class Items {

    private String itemName;
    private String rackLocation;

    public String getRackLocation() {
        return rackLocation;
    }

    public void setRackLocation(String rackLocation) {
        this.rackLocation = rackLocation;
    }

    public Items(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
