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
    private ArrayList<XpertViewData> xpertViewDataArrayList;

    public XpertViewAdapter(XpertListActivity xpertListActivity, ArrayList<XpertViewData> xpertViewDataArrayList) {
        this.xpertListActivity = xpertListActivity;
        this.xpertViewDataArrayList = xpertViewDataArrayList;
    }

    @NonNull
    @Override
    public XpertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_xpert_view, parent, false);
        return (new XpertViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull XpertViewHolder holder, int position) {
        XpertViewData xpertViewData = xpertViewDataArrayList.get(position);

        holder.textView.setText(xpertViewData.getXpertProfileName());
        Picasso.get().load("https://xpert-firebase-images.s3.ap-south-1.amazonaws.com/2018/10/aamir-khan-2-150x150.jpg").into(holder.circleImageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xpertListActivity.showXpertChat();
            }
        });
    }

    @Override
    public int getItemCount() {
        return xpertViewDataArrayList.size();
    }
}