package com.pabitrarista.chatdialog.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView leftMsgTextView;
    TextView rightMsgTextView;

    ImageView leftImageView;

    ImageView leftPlay, leftPause;
    RelativeLayout relativeLayout;
    YouTubePlayerView youTubePlayerView;

    ChatViewHolder(View itemView) {
        super(itemView);

        leftMsgTextView = itemView.findViewById(R.id.view_chat_item_left);
        rightMsgTextView = itemView.findViewById(R.id.view_chat_item_right);

        leftImageView = itemView.findViewById(R.id.view_chat_item_image);

        leftPlay = itemView.findViewById(R.id.view_chat_item_play);
        leftPause = itemView.findViewById(R.id.view_chat_item_pause);
        relativeLayout = itemView.findViewById(R.id.view_chat_item_relative_layout);
        youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);
    }
}
