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
public class BookTitlesAdapter extends BaseAdapter{

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
        //notifyItemChanged(pos);
        notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TitleHolder holder;
        if(convertView == null){
            view = inflater.inflate(R.layout.items_title_listview,null);
            holder = new TitleHolder();
            holder.title = (TextView) view.findViewById(R.id.items_title_listview_text);
            holder.focus = (ImageView) view.findViewById(R.id.items_title_listview_img);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (TitleHolder) view.getTag();
        }
        holder.title.setText(infos.get(position).getTitle());
        if(selectedPosition == position){
            holder.focus.setVisibility(View.VISIBLE);
        }else{
            holder.focus.setVisibility(View.GONE);
        }
        return view;
    }

    static class TitleHolder{
        TextView title;
        ImageView focus;
    }
}
