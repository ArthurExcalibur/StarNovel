package com.excalibur.starnovel.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.excalibur.starnovel.R;
import com.excalibur.starnovel.bean.TitleInfo;
import com.excalibur.starnovel.view.ThemeChangeableImageView;
import com.excalibur.starnovel.view.ThemeChangeableTextView;

import java.util.List;

/**
 * Created by Excalibur on 2016/12/6.
 * 阅读界面的目录ListView的Adapter
 */
public class BookTitlesAdapter extends BaseAdapter{

    private List<TitleInfo> infos;
    private Context mContext;

    private int selectedPosition;

    public BookTitlesAdapter(Context context,List<TitleInfo> infos,int pos){
        this.infos = infos;
        mContext = context;
        selectedPosition = pos;
    }

    public void setItemSelected(int pos){
        selectedPosition = pos;
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
            view = View.inflate(mContext,R.layout.items_title_listview,null);
            holder = new TitleHolder();
            holder.titleView = (ThemeChangeableTextView) view.findViewById(R.id.items_title_listview_text);
            holder.imageView = (ThemeChangeableImageView) view.findViewById(R.id.items_title_listview_img);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (TitleHolder) view.getTag();
        }
        holder.titleView.setText(infos.get(position).getTitle());
        if(position == selectedPosition){
            holder.imageView.setVisibility(View.VISIBLE);
        }else{
            holder.imageView.setVisibility(View.GONE);
        }
        return view;
    }

    public final class TitleHolder{
        ThemeChangeableTextView titleView;
        ThemeChangeableImageView imageView;
    }
}
