package ru.obsession.merchandising.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ru.obsession.merchandising.R;

public class OrderAdapter extends ArrayAdapter<Goods> {

    private final LayoutInflater lInflater;

    public OrderAdapter(Context context, List<Goods> objects) {
        super(context, R.layout.order_item, objects);
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private class ViewHolder {
        TextView textDescription;
        TextView textName;
        EditText numberOrder;
        CheckBox needOrder;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = lInflater.inflate(R.layout.order_item, null);
            holder.textName = (TextView) v.findViewById(R.id.textName);
            holder.textDescription = (TextView) v.findViewById(R.id.textDescription);
            holder.needOrder = (CheckBox) v.findViewById(R.id.checkBox);
            holder.numberOrder = (EditText) v.findViewById(R.id.textNumberOrder);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final Goods goods = getItem(position);
        holder.textName.setText(goods.nameCompany);
        holder.textDescription.setText(goods.description);
        holder.numberOrder.setText(goods.orderNumber);
        holder.numberOrder.setEnabled(goods.needOrder);
        holder.numberOrder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    goods.orderNumber = holder.numberOrder.getText().toString();
                }
            }
        });
        holder.needOrder.setChecked(goods.needOrder);
        holder.needOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goods.needOrder = !goods.needOrder;
                holder.numberOrder.setEnabled(goods.needOrder);
            }
        });
        return v;
    }
}
