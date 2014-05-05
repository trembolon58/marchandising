package ru.obsession.merchandising.report;

import java.io.Serializable;

public class Goods implements Serializable {
    public String nameCompany;
    public int clientId;
    public int id;
    public String description;
    public String name;
    public String company;
    public String weight;
    public String format;
    public String cost;
    public String faces;
    public String residue;
    public String place;
    public String shopName;
    public String visyak;
    public String returned;
    public boolean needOrder;
    public String orderNumber;

    public Goods (){}

    public void calcDescription(){
        nameCompany = name + " " + company;
        description = format + " " + weight;
    }
    @Override
    public String toString() {
        return name;
    }

    boolean isFiel() {
        return visyak != null || faces != null || cost != null || returned != null;
    }
}
