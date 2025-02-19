package chat.xpert.user.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<ChatViewData> chatViewDataList;

    public ChatViewAdapter(List<ChatViewData> chatViewDataList) {
        this.chatViewDataList = new ArrayList<>(chatViewDataList);
    }

    public int addChatData(final ChatViewData viewData) {
        chatViewDataList.add(viewData);
        notifyItemInserted(chatViewDataList.size());
        return chatViewDataList.size() - 1;
    }

    public void updateItemAtPos(final ChatViewData viewData, int position) {
        chatViewDataList.set(position, viewData);
        notifyItemChanged(position);
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

        ChatViewData chatViewData1 = chatViewDataList.get(position);

        // If the message is still pending show placeholder
        if (chatViewData1.MSG_TYPE_PACEHOLDER.equalsIgnoreCase(chatViewData1.getMsgType())) {
            holder.gifImageViewLoading.setVisibility(VISIBLE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }

        // If the message type is blank, show nothing
        if (chatViewData1.getMsgType().equalsIgnoreCase("")) {
            holder.gifImageViewLoading.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }

        // If the message is a received message.
        if (chatViewData1.MSG_TYPE_RECEIVED.equals(chatViewData1.getMsgType())) {
            holder.leftMsgTextView.setText(chatViewData1.getMsgContent());
            holder.gifImageViewLoading.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(VISIBLE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }
        // If the message is a sent message.
        else if (chatViewData1.MSG_TYPE_SENT.equals(chatViewData1.getMsgType())) {
            holder.rightMsgTextView.setText(chatViewData1.getMsgContent());
            holder.gifImageViewLoading.setVisibility(GONE);
            holder.rightMsgTextView.setVisibility(VISIBLE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }
        // If the message is an image message.
        else if (chatViewData1.MSG_TYPE_IMAGE.equals(chatViewData1.getMsgType())) {
            //Load Image using Picasso library
            Picasso.get().load(chatViewData1.getMsgContent()).into(holder.leftImageView);
            holder.gifImageViewLoading.setVisibility(GONE);
            holder.leftImageView.setVisibility(VISIBLE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.relativeLayout.setVisibility(GONE);
        }
        // If the message is a video message.
        else if (chatViewData1.MSG_TYPE_VIDEO.equals(chatViewData1.getMsgType())) {
            final String videoId = chatViewData1.getMsgContent();
            final int startSeconds = chatViewData1.getStartSeconds();
            final int endSeconds = chatViewData1.getEndSeconds();
            final RelativeLayout relativeLayoutPlay = holder.playRelativeLayout;
            final RelativeLayout relativeLayoutPause = holder.pauseRelativeLayout;
            final GifImageView gifImageView = holder.gifImageView;
            holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull final YouTubePlayer youTubePlayer) {

                    youTubePlayer.cueVideo(videoId, startSeconds);
//                    youTubePlayer.loadVideo(videoId, 88);
//                    youTubePlayer.play();
//                    youTubePlayer.pause();


                    relativeLayoutPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            relativeLayoutPlay.setVisibility(View.GONE);
                            relativeLayoutPause.setVisibility(View.VISIBLE);
                            gifImageView.setVisibility(View.VISIBLE);
                            youTubePlayer.play();
                        }
                    });

                    relativeLayoutPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            relativeLayoutPause.setVisibility(View.GONE);
                            relativeLayoutPlay.setVisibility(View.VISIBLE);
                            gifImageView.setVisibility(View.GONE);
                            youTubePlayer.pause();
                        }
                    });
                }
            });

            holder.gifImageViewLoading.setVisibility(GONE);
            holder.relativeLayout.setVisibility(VISIBLE);
            holder.rightMsgTextView.setVisibility(GONE);
            holder.leftMsgTextView.setVisibility(GONE);
            holder.leftImageView.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatViewDataList.size();
    }
}
