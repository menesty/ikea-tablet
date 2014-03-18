package org.menesty.ikea.tablet.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Menesty on 12/6/13.
 */
public class ProductItem implements Parcelable {

    public ProductItem(Parcel in) {
        artNumber = in.readString();
        productName = in.readString();
        count = in.readDouble();
        price = in.readDouble();
        weight = in.readDouble();
        orderId = in.readInt();
        shortName = in.readString();
    }

    public ProductItem(String productId, String productName, String shortName, double count, double price, double weight, int orderId) {
        this.artNumber = productId;
        this.productName = productName;
        this.shortName = shortName;
        this.count = count;
        this.price = price;
        this.weight = weight;
        this.orderId = orderId;
    }

    public String artNumber;

    public String productName;

    public double count;

    public double price;

    public double weight;

    public int orderId;

    public String shortName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artNumber);
        parcel.writeString(productName);
        parcel.writeDouble(count);
        parcel.writeDouble(price);
        parcel.writeDouble(weight);
        parcel.writeInt(orderId);
        parcel.writeString(shortName);
    }
}
