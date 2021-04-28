package com.alon.imagerecognitionapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alon.imagerecognitionapp.Models.RecognitionItem;
import com.alon.imagerecognitionapp.R;

import java.util.List;

public class RecognitionItemAdapter extends RecyclerView.Adapter<RecognitionItemAdapter.MyViewHolder> {

    private List<RecognitionItem> dataSet;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView row_LBL_name, row_LBL_match;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            row_LBL_name = itemView.findViewById(R.id.row_LBL_name);
            row_LBL_match = itemView.findViewById(R.id.row_LBL_match);
        }
    }

    public RecognitionItemAdapter(List<RecognitionItem> dataSet){
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        RecognitionItemAdapter.MyViewHolder vh = new RecognitionItemAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.row_LBL_name.setText(dataSet.get(position).getName());
        holder.row_LBL_match.setText(String.valueOf(dataSet.get(position).getMatch()) + "%");
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
