package chat.xpert.user.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import chat.xpert.user.AskScreen2Activity;
import com.pabitrarista.chatdialog.R;

import java.util.ArrayList;

public class BucketViewAdapter extends RecyclerView.Adapter<BucketViewAdapter.BucketViewHolder> {

    private AskScreen2Activity askScreen2Activity;
    private ArrayList<String> arrayList;
    private Context mContext;
    private int select = -1;


    public BucketViewAdapter(AskScreen2Activity askScreen2Activity, ArrayList<String> arrayList
            , Context mContext) {
        this.askScreen2Activity = askScreen2Activity;
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(askScreen2Activity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.layout_bucket_view, parent, false);
        return new BucketViewAdapter.BucketViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final BucketViewHolder holder, final int position) {
        holder.textView.setText(arrayList.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = position;
                setQuestion(holder, position);


            }
        });

        if(select==position){
            holder.textView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_design_13));
            holder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));


        }else {

            holder.textView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_design_2));
            holder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));

        }
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setQuestion(BucketViewHolder holder, int position ) {
//        Toast.makeText(askScreen2Activity, holder.textView.getText().toString(), Toast.LENGTH_SHORT).show();
        askScreen2Activity.setQuestion(holder.textView.getText().toString());

        holder.textView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.bg_design_13));
        holder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));





    }

    class BucketViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        BucketViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.bucket_view_text_view);


        }
    }
}
