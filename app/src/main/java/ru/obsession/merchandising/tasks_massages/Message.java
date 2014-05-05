package ru.obsession.merchandising.tasks_massages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String text;
    public String date;
    public void getDate(String timeStampStr){

        try{
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(Long.parseLong(timeStampStr) * 1000));
            date = sdf.format(netDate);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
