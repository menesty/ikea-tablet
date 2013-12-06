package org.menesty.ikea.tablet.domain;

public class AvailableProductItem {

    public AvailableProductItem(String productName, int count, double price, int weight) {
        this.price = price;
        this.productName = productName;
        this.count = count;
        this.weight = weight;
    }

    public String productName;

    public int count;

    public int weight;

    public double price;
}
