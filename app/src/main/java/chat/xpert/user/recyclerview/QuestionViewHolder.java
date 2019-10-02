package chat.xpert.user.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.R;

class QuestionViewHolder extends RecyclerView.ViewHolder {

    TextView textView;

    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.question_view_text_view);
    }
}