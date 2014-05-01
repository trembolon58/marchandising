package ru.obsession.merchandising.tasks_massages;

public class Task extends Message {
    public static final int NEW = 0;
    public static final int READED = 1;
    public static final int TAKED = 2;
    public static final int REFUSED = 3;
    public String status;
    public int statusCode;
    public int id;
}
