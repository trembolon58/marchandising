package ru.obsession.merchandising.works;

import android.os.Parcel;
import android.os.Parcelable;

public class Work implements Parcelable{

    String name;
    String description;

    public Work(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public static final Parcelable.Creator<Work> CREATOR;

    static {
        CREATOR = new Creator<Work>() {

            @Override
            public Work createFromParcel(Parcel source) {
                return new Work(source);
            }

            @Override
            public Work[] newArray(int size) {
                return new Work[size];
            }
        };
    }

    private Work(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        description = in.readString();
    }
}