package org.menesty.ikea.tablet.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class AvailableProductItem implements Parcelable {
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

    public AvailableProductItem(Parcel in) {
        shortName = in.readString();
        productId = in.readString();
        productName = in.readString();
        price = in.readDouble();
        count = in.readDouble();
        weight = in.readDouble();
        boolean[] value = new boolean[3];
        in.readBooleanArray(value);
        zestav = value[0];
        allowed = value[1];
        visible = value[2];
        orderId = in.readInt();
    }

    public boolean zestav;

    public boolean allowed;

    public boolean visible;

    public int orderId;

    public String shortName;

    public String productId;

    public int id;

    public String productName;

    public double count;

    public double weight;

    public double price;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(shortName);
        parcel.writeString(productId);
        parcel.writeString(productName);
        parcel.writeDouble(price);
        parcel.writeDouble(count);
        parcel.writeDouble(weight);
        parcel.writeBooleanArray(new boolean[]{zestav, allowed, visible});
        parcel.writeInt(orderId);
    }
}
