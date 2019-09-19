package com.pabitrarista.chatdialog.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;
import com.pabitrarista.chatdialog.XpertListActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class XpertViewAdapter extends RecyclerView.Adapter<XpertViewHolder> {

    private XpertListActivity xpertListActivity;
    private ArrayList<String> xpertName;
    private ArrayList<String> xpertImage;

    public XpertViewAdapter(XpertListActivity xpertListActivity, ArrayList<String> xpertName, ArrayList<String> xpertImage) {
        this.xpertListActivity = xpertListActivity;
        this.xpertName = xpertName;
        this.xpertImage = xpertImage;
    }

    @NonNull
    @Override
    public XpertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_xpert_view, parent, false);
        return (new XpertViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final XpertViewHolder holder, final int position) {

        holder.textView.setText(xpertName.get(position));
        try {
            Picasso.get().load(xpertImage.get(position)).into(holder.circleImageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.icon_dp).into(holder.circleImageView);
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xpertListActivity.showXpertChat(xpertName.get(position), xpertImage.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return xpertName.size();
    }
}