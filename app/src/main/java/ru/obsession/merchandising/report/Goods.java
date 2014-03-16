package ru.obsession.merchandising.report;

import java.io.Serializable;

public class Goods implements Serializable {
    String nameCompany;
    int id;
    String description;
    String name;
    String company;
    String weight;
    String format;
    String cost;
    String faces;
    String recidue;
    String place;

    public Goods(int id, String name, String company, String weight, String format) {
        this.name = name;
        this.id = id;
        nameCompany = name + " " + company;
        description = format + " " + weight;
        this.company = company;
        this.weight = weight;
        this.format = format;
    }

    @Override
    public String toString() {
        return name;
    }

    boolean isFiel() {
        return place != null && faces != null && cost != null && recidue != null;
    }
}
