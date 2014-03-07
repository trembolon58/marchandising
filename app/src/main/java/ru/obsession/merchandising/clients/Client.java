package ru.obsession.merchandising.clients;

import android.os.Parcel;
import android.os.Parcelable;

public class Client implements Parcelable {
    int id;
    String name;
    boolean done;

    public Client(int id, String name, boolean done) {
        this.id = id;
        this.done = done;
        this.name = name;
    }

    public static final Parcelable.Creator<Client> CREATOR;

    static {
        CREATOR = new Creator<Client>() {

            @Override
            public Client createFromParcel(Parcel source) {
                return new Client(source);
            }

            @Override
            public Client[] newArray(int size) {
                return new Client[size];
            }
        };
    }

    private Client(Parcel in) {
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
    }

    private void readFromParcel(Parcel in) {
        done = in.readByte() != 0;
        name = in.readString();
    }
}
