package com.ggp.ai_ggpmobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ggp.ai_ggpmobile.Model.Chopper;
import com.ggp.ai_ggpmobile.R;

import java.text.DecimalFormat;
import java.util.List;

public class ChopperAdapter extends RecyclerView.Adapter<ChopperAdapter.ListViewHolder> {

    private Context context;
    private List<Chopper> list;
    double bt, th, ar, total;

    public ChopperAdapter(Context context, List<Chopper> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chopper, parent, false);
        return new ChopperAdapter.ListViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Chopper chopper = list.get(position);

        double total = Double.parseDouble(chopper.getTotal());
        bt = Double.parseDouble(chopper.getBt());
        th = Double.parseDouble(chopper.getTh());
        ar = Double.parseDouble(chopper.getAr());
        total = Double.parseDouble(chopper.getTotal());

        if (position%2==0){
            holder.tvPlot.setBackgroundResource(R.color.dark_green);
            holder.tvBt.setBackgroundResource(R.color.dark_green);
            holder.tvTh.setBackgroundResource(R.color.dark_green);
            holder.tvAr.setBackgroundResource(R.color.dark_green);
            if (total<85){
                holder.tvTotal.setBackgroundResource(R.color.red);
            }else {
                holder.tvTotal.setBackgroundResource(R.color.dark_green);
            }
        } else if (total <85){
            holder.tvTotal.setBackgroundResource(R.color.red);
        }

        DecimalFormat df = new DecimalFormat("##.##");
        holder.tvPlot.setText(chopper.getPlot());
        holder.tvBt.setText(df.format(bt)+"%");
        holder.tvTh.setText(df.format(th)+"%");
        holder.tvAr.setText(df.format(ar)+"%");
        //sum = Double.parseDouble(chopper.getBt())+Double.parseDouble(chopper.getTh())+Double.parseDouble(chopper.getAr());
        holder.tvTotal.setText(df.format(total)+"%");

        holder.itemView.setOnClickListener(v -> onItemClickCallback.onItemClicked(list.get(holder.getAdapterPosition())));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView tvPlot, tvBt, tvTh, tvAr, tvTotal;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPlot = itemView.findViewById(R.id.tv_plot);
            tvBt = itemView.findViewById(R.id.tv_bt);
            tvTh = itemView.findViewById(R.id.tv_th);
            tvAr = itemView.findViewById(R.id.tv_ar);
            tvTotal = itemView.findViewById(R.id.tv_total);
        }
    }

    private ChopperAdapter.OnItemClickCallback onItemClickCallback;

    public void onItemClickCallback (ChopperAdapter.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClicked(Chopper chopper);
    }

    public interface dataChopper{
        void chopper(Chopper chopper);
    }
}
