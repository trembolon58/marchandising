package ru.obsession.merchandising.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        TextView textCost;
        TextView textFaces;
        TextView textRecedue;
        TextView textPlace;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.face_report_item, null);
            holder.textName = (TextView) v.findViewById(R.id.textName);
            holder.textDescription = (TextView) v.findViewById(R.id.textDescription);
            holder.textCost = (TextView) v.findViewById(R.id.textCost);
            holder.textFaces = (TextView) v.findViewById(R.id.textNumFaces);
            holder.textRecedue = (TextView) v.findViewById(R.id.textResidue);
            holder.textPlace = (TextView) v.findViewById(R.id.textPlace);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Goods goods = getItem(position);
        holder.textName.setText(goods.nameCompany);
        holder.textDescription.setText(goods.description);
        holder.textCost.setText(goods.cost);
        holder.textFaces.setText(goods.faces);
        holder.textRecedue.setText(goods.recidue);
        holder.textPlace.setText(goods.place);
        return v;
    }
}
