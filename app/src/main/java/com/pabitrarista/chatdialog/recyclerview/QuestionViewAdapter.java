package com.pabitrarista.chatdialog.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabitrarista.chatdialog.AskScreen2Activity;
import com.pabitrarista.chatdialog.R;

import java.util.ArrayList;

public class QuestionViewAdapter extends RecyclerView.Adapter<QuestionViewHolder> {

    private AskScreen2Activity askScreen2Activity;
    private ArrayList<String> arrayList;

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
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        holder.textView.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
