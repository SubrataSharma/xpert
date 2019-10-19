package chat.xpert.user.recyclerview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;

import de.hdodenhof.circleimageview.CircleImageView;

class XpertViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout relativeLayout;
    CircleImageView circleImageView;
    TextView textView, textView2;

    XpertViewHolder(View itemView) {
        super(itemView);

        relativeLayout = itemView.findViewById(R.id.xpert_view_relative_layout);
        circleImageView = itemView.findViewById(R.id.xpert_view_profile_image);
        textView = itemView.findViewById(R.id.xpert_view_profile_name);
        textView2 = itemView.findViewById(R.id.xpert_view_profile_bio);
    }
}
