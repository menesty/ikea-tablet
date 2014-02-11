package org.menesty.ikea.tablet.domain;

public class AvailableProductItem {
    public AvailableProductItem(String productId, String productName, String shortName, double count, double price, double weight, boolean zestav, boolean allowed, boolean visible, int orderId) {
        this.shortName = shortName;
        this.productId = productId;
        this.price = price;
        this.productName = productName;
        this.count = count;
        this.weight = weight;
        this.zestav = zestav;
        this.allowed = allowed;
        this.visible = visible;
        this.orderId = orderId;

    }

    public final boolean zestav;
    public final boolean allowed;
    public final boolean visible;
    public final int orderId;

    public String shortName;

    public String productId;

    public int id;

    public String productName;

    public double count;

    public double weight;

    public double price;
}
