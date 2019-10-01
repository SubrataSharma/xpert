package com.pabitrarista.chatdialog.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import pl.droidsonroids.gif.GifImageView;

class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView leftMsgTextView;
    TextView rightMsgTextView;

    ImageView leftImageView;

    RelativeLayout relativeLayout, playRelativeLayout, pauseRelativeLayout;
    YouTubePlayerView youTubePlayerView;
    GifImageView gifImageView, gifImageViewLoading;

    ChatViewHolder(View itemView) {
        super(itemView);

        gifImageViewLoading = itemView.findViewById(R.id.view_chat_item_loading);

        leftMsgTextView = itemView.findViewById(R.id.view_chat_item_left);
        rightMsgTextView = itemView.findViewById(R.id.view_chat_item_right);

        leftImageView = itemView.findViewById(R.id.view_chat_item_image);

        relativeLayout = itemView.findViewById(R.id.view_chat_item_relative_layout);
        playRelativeLayout = itemView.findViewById(R.id.view_chat_item_play_layout);
        pauseRelativeLayout = itemView.findViewById(R.id.view_chat_item_pause_layout);
        youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);
        gifImageView = itemView.findViewById(R.id.view_chat_item_play_pause_gif);
    }
}
