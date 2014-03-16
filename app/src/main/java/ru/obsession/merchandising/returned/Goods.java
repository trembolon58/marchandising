package ru.obsession.merchandising.returned;

import java.io.Serializable;

public class Goods implements Serializable{
    String nameCompany;
    int id;
    String description;
    String name;
    String company;
    String weight;
    String format;
    String count;
    String reasone;

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

    public boolean isFiel() {
        return count != null && reasone != null;
    }

  /*  boolean isFiel() {
        return place != null && faces != null && cost != null && recidue != null;
    }*/
}
