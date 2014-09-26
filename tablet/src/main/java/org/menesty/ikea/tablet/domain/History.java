package org.menesty.ikea.tablet.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Menesty on
 * 9/7/14.
 * 9:26.
 */
public class History implements Parcelable {
    private int id;
    private String actionId;
    private double price;
    private Date createDate;

    public History(Parcel in) {
        id = in.readInt();
        actionId = in.readString();
        price = in.readDouble();
        createDate = new Date(in.readLong());
    }

    public History(int id, String actionId, double price, Date createDate) {
        this.id = id;
        this.actionId = actionId;
        this.price = price;
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(actionId);
        parcel.writeDouble(price);
        parcel.writeLong(createDate.getTime());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
