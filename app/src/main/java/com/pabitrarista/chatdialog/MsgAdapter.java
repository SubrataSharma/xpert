package com.pabitrarista.chatdialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MsgAdapter extends RecyclerView.Adapter<MsgViewHolder> {
    private List<MsgSendReceive> msgDtoList = null;

    public MsgAdapter(List<MsgSendReceive> msgDtoList) {
        this.msgDtoList = msgDtoList;
    }

    @Override
    public void onBindViewHolder(MsgViewHolder holder, int position) {
        MsgSendReceive msgDto = this.msgDtoList.get(position);
        // If the message is a received message.
        if (msgDto.MSG_TYPE_RECEIVED.equals(msgDto.getMsgType())) {
            // Show received message in left linearlayout.
            holder.leftMsgTextView.setVisibility(VISIBLE);
            holder.leftMsgTextView.setText(msgDto.getMsgContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
        }
        // If the message is a sent message.
        else if (msgDto.MSG_TYPE_SENT.equals(msgDto.getMsgType())) {
            // Show sent message in right linearlayout.
            holder.rightMsgTextView.setVisibility(VISIBLE);
            holder.rightMsgTextView.setText(msgDto.getMsgContent());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
        }
        // If the message is an image message.
        else if (msgDto.MSG_TYPE_IMAGE.equals(msgDto.getMsgType())) {
            //Load Image using Picasso library
            Picasso.get().load(msgDto.getMsgContent()).into(holder.leftImageView);

            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(VISIBLE);
        }
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_chat_item, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (msgDtoList == null) {
            msgDtoList = new ArrayList<MsgSendReceive>();
        }
        return msgDtoList.size();
    }

}
