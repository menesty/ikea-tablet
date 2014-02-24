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
    }

    public ProductItem(String productId, String productName, double count, double price, double weight) {
        this.artNumber = productId;
        this.productName = productName;
        this.count = count;
        this.price = price;
        this.weight = weight;
    }

    public String artNumber;

    public String productName;

    public double count;

    public double price;

    public double weight;

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
    }
}
