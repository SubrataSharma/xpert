package com.pabitrarista.chatdialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MsgViewHolder extends RecyclerView.ViewHolder {

    TextView leftMsgTextView;
    TextView rightMsgTextView;
    ImageView leftImageView;
    ImageView leftPlay, leftPause;
    RelativeLayout relativeLayout;
    YouTubePlayerView youTubePlayerView;

    public MsgViewHolder(View itemView) {
        super(itemView);

        leftMsgTextView = itemView.findViewById(R.id.view_chat_item_left);
        rightMsgTextView = itemView.findViewById(R.id.view_chat_item_right);
        leftImageView = itemView.findViewById(R.id.view_chat_item_image);
        leftPlay = itemView.findViewById(R.id.view_chat_item_play);
        leftPause = itemView.findViewById(R.id.view_chat_item_pause);
        relativeLayout = itemView.findViewById(R.id.view_chat_item_relative_layout);
        youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);

        leftPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftPlay.setVisibility(View.GONE);
                leftPause.setVisibility(View.VISIBLE);
            }
        });
        leftPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftPause.setVisibility(View.GONE);
                leftPlay.setVisibility(View.VISIBLE);
            }
        });
    }
}
