package com.excalibur.starnovel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.excalibur.starnovel.R;

/**
 * Created by Excalibur on 2017/2/7.
 * 信息界面RecyclerView的Adapter
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View footerView;
    public static final int TYPE_FOOTER = 10;

    private LayoutInflater inflater;

    public MessageAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){
            return new RecyclerView.ViewHolder(footerView){};
        }
        View view = inflater.inflate(R.layout.items_message,parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hol, int position) {
        if(getItemViewType(position) == TYPE_FOOTER){
            return;
        }
        MessageHolder holder = (MessageHolder) hol;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    static class MessageHolder extends RecyclerView.ViewHolder{

        ImageView user;
        TextView title;
        TextView content;
        TextView date;

        public MessageHolder(View itemView) {
            super(itemView);
            user = (ImageView) itemView.findViewById(R.id.items_message_user);
            title = (TextView) itemView.findViewById(R.id.items_message_title);
            content = (TextView) itemView.findViewById(R.id.items_message_content);
            date = (TextView) itemView.findViewById(R.id.items_message_date);
        }
    }
}
