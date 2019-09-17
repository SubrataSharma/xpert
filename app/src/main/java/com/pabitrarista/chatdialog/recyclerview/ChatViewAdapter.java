package com.pabitrarista.chatdialog.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<ChatViewData> chatViewData;

    public ChatViewAdapter(List<ChatViewData> chatViewData) {
        this.chatViewData = chatViewData;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_chat_view, parent, false);
        return (new ChatViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        ChatViewData chatViewData1 = chatViewData.get(position);

        // If the message is a received message.
        if (chatViewData1.MSG_TYPE_RECEIVED.equals(chatViewData1.getMsgType())) {
            holder.leftMsgTextView.setText(chatViewData1.getMsgContent());
            holder.leftMsgTextView.setVisibility(VISIBLE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }
        // If the message is a sent message.
        else if (chatViewData1.MSG_TYPE_SENT.equals(chatViewData1.getMsgType())) {
            holder.rightMsgTextView.setText(chatViewData1.getMsgContent());
            holder.rightMsgTextView.setVisibility(VISIBLE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }
        // If the message is an image message.
        else if (chatViewData1.MSG_TYPE_IMAGE.equals(chatViewData1.getMsgType())) {
            //Load Image using Picasso library
            Picasso.get().load(chatViewData1.getMsgContent()).into(holder.leftImageView);
            holder.leftImageView.setVisibility(VISIBLE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }
        // If the message is a video message.
        else if (chatViewData1.MSG_TYPE_VIDEO.equals(chatViewData1.getMsgType())) {
            final String videoId = chatViewData1.getMsgContent();
            final int startSeconds = chatViewData1.getStartSeconds();
            final ImageView leftPlay = holder.leftPlay;
            final ImageView leftPause = holder.leftPause;
            holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull final YouTubePlayer youTubePlayer) {

                    youTubePlayer.cueVideo(videoId, startSeconds);
//                    youTubePlayer.loadVideo(videoId, 88);
//                    youTubePlayer.play();
//                    youTubePlayer.pause();

                    leftPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            leftPlay.setVisibility(View.GONE);
                            leftPause.setVisibility(View.VISIBLE);
                            youTubePlayer.play();
                        }
                    });
                    leftPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            leftPause.setVisibility(View.GONE);
                            leftPlay.setVisibility(View.VISIBLE);
                            youTubePlayer.pause();
                        }
                    });
                }
            });

            holder.relativeLayout.setVisibility(VISIBLE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatViewData.size();
    }
}
