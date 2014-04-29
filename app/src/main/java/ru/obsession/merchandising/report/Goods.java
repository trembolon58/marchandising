package ru.obsession.merchandising.report;

import java.io.Serializable;

public class Goods implements Serializable {
    public String nameCompany;
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
    public String retured;

    public Goods(int id, String name, String company, String weight, String format) {
        this.name = name;
        this.id = id;
        nameCompany = name + " " + company;
        description = format + " " + weight;
        this.company = company;
        this.weight = weight;
        this.format = format;
    }

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
        return place != null && faces != null && cost != null && residue != null;
    }
}
