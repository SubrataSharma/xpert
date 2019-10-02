package chat.xpert.user.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import chat.xpert.user.AskScreen2Activity;
import com.pabitrarista.chatdialog.R;

import java.util.ArrayList;

public class QuestionViewAdapter extends RecyclerView.Adapter<QuestionViewHolder> {

    private AskScreen2Activity askScreen2Activity;
    private ArrayList<String> arrayList;
    private int backgroundImage = 0;

    public QuestionViewAdapter(AskScreen2Activity askScreen2Activity, ArrayList<String> arrayList) {
        this.askScreen2Activity = askScreen2Activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(askScreen2Activity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.layout_question_view, parent, false);
        return (new QuestionViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionViewHolder holder, final int position) {
        holder.textView.setText(arrayList.get(position));

        if (backgroundImage == 0) {
            holder.textView.setBackgroundResource(R.drawable.image_speech_bubble_2);
            backgroundImage = 1;
        } else if (backgroundImage == 1) {
            holder.textView.setBackgroundResource(R.drawable.image_speech_bubble_reverse_2);
            backgroundImage = 0;
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnswer(holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setAnswer(QuestionViewHolder holder, int position) {
//        Toast.makeText(askScreen2Activity, holder.textView.getText().toString(), Toast.LENGTH_SHORT).show();
        askScreen2Activity.setAnswer(holder.textView.getText().toString());
    }
}
