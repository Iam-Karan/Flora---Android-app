package com.flora.flora;

public class ProductItemData {

    private String itemName;
    private String itemPrice;

    public ProductItemData(String itemName, String itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

}
