package com.flora.flora;

public class CartModel {
    String productid;
    String quantity;

    public  CartModel(){

    }
    public CartModel(String productid, String quantity) {
        this.productid = productid;
        this.quantity = quantity;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
