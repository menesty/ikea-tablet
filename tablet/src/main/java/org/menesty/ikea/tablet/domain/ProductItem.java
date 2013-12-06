package org.menesty.ikea.tablet.domain;

/**
 * Created by Menesty on 12/6/13.
 */
public class ProductItem {

    public ProductItem(String productId) {
        this.artNumber = productId;
    }

    public String artNumber;

    public int count;

    public double price;

    public int weight;

}
