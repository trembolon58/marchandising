package ru.obsession.merchandising.shops;

import android.os.Parcel;
import android.os.Parcelable;

public class Shop implements Parcelable{
    public int id;
    public String name;
    public String address;
    public boolean done;

    public Shop(int id, String name, String adress, boolean done) {
        this.id = id;
        this.done = done;
        this.name = name;
        this.address = adress;
    }
    public static final Parcelable.Creator<Shop> CREATOR;

    static {
        CREATOR = new Creator<Shop>() {

            @Override
            public Shop createFromParcel(Parcel source) {
                return new Shop(source);
            }

            @Override
            public Shop[] newArray(int size) {
                return new Shop[size];
            }
        };
    }

    private Shop(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeString(name);
        dest.writeString(address);
    }

    private void readFromParcel(Parcel in) {
        done = in.readByte() != 0;
        name = in.readString();
        address = in.readString();
    }
}
