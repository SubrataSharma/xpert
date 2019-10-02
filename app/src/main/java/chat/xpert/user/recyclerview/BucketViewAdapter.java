package chat.xpert.user.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import chat.xpert.user.AskScreen2Activity;
import com.pabitrarista.chatdialog.R;

import java.util.ArrayList;

public class BucketViewAdapter extends RecyclerView.Adapter<BucketViewHolder> {

    private AskScreen2Activity askScreen2Activity;
    private ArrayList<String> arrayList;

    public BucketViewAdapter(AskScreen2Activity askScreen2Activity, ArrayList<String> arrayList) {
        this.askScreen2Activity = askScreen2Activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public BucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(askScreen2Activity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.layout_bucket_view, parent, false);
        return (new BucketViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final BucketViewHolder holder, final int position) {
        holder.textView.setText(arrayList.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setQuestion(holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setQuestion(BucketViewHolder holder, int position) {
//        Toast.makeText(askScreen2Activity, holder.textView.getText().toString(), Toast.LENGTH_SHORT).show();
        askScreen2Activity.setQuestion(holder.textView.getText().toString());
    }
}
