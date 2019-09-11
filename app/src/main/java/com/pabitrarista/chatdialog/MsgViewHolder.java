package com.pabitrarista.chatdialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MsgViewHolder extends RecyclerView.ViewHolder {

    TextView leftMsgTextView;
    TextView rightMsgTextView;
    ImageView leftImageView;

    public MsgViewHolder(View itemView) {
        super(itemView);

        leftMsgTextView = itemView.findViewById(R.id.view_chat_item_left);
        rightMsgTextView = itemView.findViewById(R.id.view_chat_item_right);
        leftImageView = itemView.findViewById(R.id.view_chat_item_image);

    }
}
