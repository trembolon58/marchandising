package ru.obsession.merchandising.shops;

import java.io.Serializable;

public class Shop implements Serializable {
    public int id;
    public String name;
    public String address;
    public boolean needOrder = true;

    public Shop() {
        needOrder = true;
    }
}
