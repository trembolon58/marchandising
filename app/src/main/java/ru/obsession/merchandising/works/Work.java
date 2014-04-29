package ru.obsession.merchandising.works;

import java.io.Serializable;

public class Work implements Serializable{

    public String name;
    public int id;
    public int shop;
    public int client;
    public int merch;
    public String desc;
    public int date_show;

    public Work(String name, String description) {
        this.name = name;
        this.desc = description;
    }

    public Work() {}
}