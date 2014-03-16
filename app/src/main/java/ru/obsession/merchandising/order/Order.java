package ru.obsession.merchandising.order;

import java.io.Serializable;

public class Order implements Serializable{
    String nameCompany;
    int id;
    String description;
    String name;
    String company;
    String weight;
    String format;
    String count;

    public Order(int id, String name, String company, String weight, String format) {
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
        return count != null && !count.equals("");
    }
}
