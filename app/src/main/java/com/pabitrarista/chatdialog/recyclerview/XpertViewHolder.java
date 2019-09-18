package com.pabitrarista.chatdialog.recyclerview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;

import de.hdodenhof.circleimageview.CircleImageView;

class XpertViewHolder extends RecyclerView.ViewHolder {

    LinearLayout linearLayout;
    CircleImageView circleImageView;
    TextView textView;

    XpertViewHolder(View itemView) {
        super(itemView);

        linearLayout = itemView.findViewById(R.id.xpert_view_linear_layout);
        circleImageView = itemView.findViewById(R.id.xpert_view_profile_image);
        textView = itemView.findViewById(R.id.xpert_view_profile_name);
    }
}
