package com.excalibur.starnovel.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.excalibur.starnovel.R;
import com.excalibur.starnovel.bean.Riji;

import java.util.List;

/**
 * 原创界面第一个页面的RecyclerView的Adapter
 * Created by Administrator on 2017/1/13.
 */
public class WriteRecyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private LayoutInflater layoutInflater;
    private Context mContext;
    private List<Riji> rijiList;

    public static final int FOOTER_TYPE = 1;
    private View footerView;

    public WriteRecyAdapter(Context context,List<Riji> list){
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        rijiList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FOOTER_TYPE){
            return new RecyclerView.ViewHolder(footerView){};
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.fra_write_items,parent,false);
        return new WriteHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hol, final int position) {
        if(footerView != null && position == rijiList.size()){
            return;
        }
        final WriteHolder holder = (WriteHolder) hol;
        Riji riji = rijiList.get(position);

        holder.user.setImageResource(R.mipmap.user);
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userClickedListener != null){
                    userClickedListener.onUserClicked();
                }
            }
        });
        String type = "";
        //holder.type.setText(type);
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareClickedListener != null){
                    shareClickedListener.onShareClicked(v);
                }
            }
        });
        if(riji.getImage() == null){
            holder.image.setVisibility(View.GONE);
        }else{
            holder.image.setVisibility(View.VISIBLE);
            //holder.image.setImageResource(R.mipmap.ic_launcher);
        }
        holder.title.setText(riji.getTitle());
        holder.content.setText(riji.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickedListener != null){
                    itemClickedListener.onItemClicked(position,holder.image);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(footerView != null && position == rijiList.size()){
            return FOOTER_TYPE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if(footerView != null){
            return rijiList.size() + 1;
        }
        return rijiList.size();
    }

    public void addFooterView(View view){
        footerView = view;
        notifyItemChanged(rijiList.size());
    }

    private OnItemClickedListener itemClickedListener;
    private OnUserClickedListener userClickedListener;
    private OnShareClickedListener shareClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener listener){
        itemClickedListener = listener;
    }

    public void setOnUserClickedListener(OnUserClickedListener listener){
        userClickedListener = listener;
    }

    public void setOnShareClickedListener(OnShareClickedListener listener){
        shareClickedListener = listener;
    }

    public interface OnItemClickedListener{
        void onItemClicked(int position,View view);
    }

    public interface OnUserClickedListener{
        void onUserClicked();
    }

    public interface OnShareClickedListener{
        void onShareClicked(View v);
    }

    public static class WriteHolder extends RecyclerView.ViewHolder{

        ImageView user;
        TextView type;
        ImageView share;
        ImageView image;
        TextView title;
        TextView content;

        public WriteHolder(View itemView) {
            super(itemView);
            user = (ImageView) itemView.findViewById(R.id.fra_write_items_touxiang);
            //type = (TextView) itemView.findViewById(R.id.fra_write_items_from);
            share = (ImageView) itemView.findViewById(R.id.fra_write_items_more);
            image = (ImageView) itemView.findViewById(R.id.fra_write_items_image);
            title = (TextView) itemView.findViewById(R.id.fra_write_items_title);
            content = (TextView) itemView.findViewById(R.id.fra_write_items_content);
        }
    }
}
