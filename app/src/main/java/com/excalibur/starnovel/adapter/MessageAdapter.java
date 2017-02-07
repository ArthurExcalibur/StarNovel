package com.excalibur.starnovel.adapter;

import android.support.v7.widget.RecyclerView;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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
