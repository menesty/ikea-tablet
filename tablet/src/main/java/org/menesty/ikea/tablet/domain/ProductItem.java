package org.menesty.ikea.tablet.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Menesty on 12/6/13.
 */
public class ProductItem implements Parcelable {

    public ProductItem(Parcel in){
        artNumber = in.readString();
        count = in.readInt();
        price = in.readDouble();
        weight = in.readInt();
    }

    public ProductItem(String productId) {
        this.artNumber = productId;
    }

    public String artNumber;

    public int count;

    public double price;

    public int weight;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artNumber);
        parcel.writeInt(count);
        parcel.writeDouble(price);
        parcel.writeInt(weight);
    }
}
