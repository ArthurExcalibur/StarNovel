package com.excalibur.starnovel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.excalibur.starnovel.R;
import com.excalibur.starnovel.bean.TitleInfo;
import com.excalibur.starnovel.view.ThemeChangeableImageView;
import com.excalibur.starnovel.view.ThemeChangeableTextView;

import java.util.List;

/**
 * Created by Excalibur on 2016/12/6.
 * 阅读界面的目录RecyclerView的Adapter
 */
public class BookTitlesAdapter extends RecyclerView.Adapter<BookTitlesAdapter.TitleHolder>{

    private List<TitleInfo> infos;
    private LayoutInflater inflater;

    private int selectedPosition;

    public BookTitlesAdapter(Context context,List<TitleInfo> infos,int pos){
        this.infos = infos;
        inflater = LayoutInflater.from(context);
        selectedPosition = pos;
    }

    public void setItemSelected(int pos){
        selectedPosition = pos;
        notifyItemChanged(pos);
        //notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    @Override
    public BookTitlesAdapter.TitleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.items_title_listview,parent,false);
        return new TitleHolder(view);
    }

    @Override
    public void onBindViewHolder(BookTitlesAdapter.TitleHolder holder, final int position) {
        if(listener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(position);
                }
            });
        }
        holder.title.setText(infos.get(position).getTitle());
        if(position == selectedPosition){
            holder.focus.setVisibility(View.VISIBLE);
        }else{
            holder.focus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClicked(int position);
    }
    public void setOnItemClickListener(OnItemClickListener l){
        listener = l;
    }

    static class TitleHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView focus;
        public TitleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.items_title_listview_text);
            focus = (ImageView) itemView.findViewById(R.id.items_title_listview_img);
        }
    }
}
