package ru.obsession.merchandising.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class FaceReportAdapter extends ArrayAdapter<Goods> {

    private final LayoutInflater lInflater;

    public FaceReportAdapter(Context context, List<Goods> objects) {
        super(context, R.layout.face_report_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        TextView textDescription;
        TextView textName;
        EditText textCost;
        EditText textFaces;
        EditText textResidue;
        EditText textVisyak;
        EditText textExchange;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.face_report_item, null);
            holder.textName = (TextView) v.findViewById(R.id.textName);
            holder.textDescription = (TextView) v.findViewById(R.id.textDescription);
            holder.textCost = (EditText) v.findViewById(R.id.textCost);
            holder.textFaces = (EditText) v.findViewById(R.id.textNumFaces);
            holder.textResidue = (EditText) v.findViewById(R.id.textResidue);
            holder.textVisyak = (EditText) v.findViewById(R.id.textVisyak);
            holder.textExchange = (EditText) v.findViewById(R.id.textReturn);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final Goods goods = getItem(position);
        holder.textName.setText(goods.nameCompany);
        holder.textDescription.setText(goods.description);
        holder.textCost.setText(goods.cost);
        holder.textCost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    goods.cost = holder.textCost.getText().toString();
                }
            }
        });
        holder.textFaces.setText(goods.faces);
        holder.textFaces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    goods.faces = holder.textFaces.getText().toString();
                }
            }
        });
        holder.textResidue.setText(goods.residue);
        holder.textResidue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    goods.residue = holder.textResidue.getText().toString();
                }
            }
        });
        holder.textVisyak.setText(goods.visyak);
        holder.textVisyak.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    goods.visyak = holder.textVisyak.getText().toString();
                }
            }
        });
        holder.textExchange.setText(goods.returned);
        holder.textExchange.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    goods.returned = holder.textExchange.getText().toString();
                }
            }
        });
        return v;
    }
}
