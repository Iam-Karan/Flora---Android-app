package com.flora.flora;

public class CartProductData {
    public ProductData productData;
    public String quantity;

    public CartProductData(ProductData productData, String quantity) {
        this.productData = productData;
        this.quantity = quantity;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
