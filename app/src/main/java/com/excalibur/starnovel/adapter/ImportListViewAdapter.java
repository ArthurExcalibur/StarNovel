package com.excalibur.starnovel.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.excalibur.starnovel.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Excalibur on 2016/11/21.
 * 导入书籍界面的ListView的Adapter
 */
public class ImportListViewAdapter extends BaseAdapter{

    private Context mContext;
    public List<Map<String,String>> dataList;

    public ImportListViewAdapter(Context context,List<Map<String,String>> data){
        mContext = context;
        dataList = data;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null){
            view = View.inflate(mContext, R.layout.items_import_listview,null);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.items_import_listview_title);
            holder.comment = (TextView) view.findViewById(R.id.items_import_listview_comment);
            holder.selected = (CheckBox) view.findViewById(R.id.items_import_listview_selected);
            holder.imported = (TextView) view.findViewById(R.id.items_import_listview_importText);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.title.setText(dataList.get(position).get("title"));
        holder.comment.setText(dataList.get(position).get("comment"));
        if(dataList.get(position).get("imported").equals("false")){
            holder.selected.setVisibility(View.VISIBLE);
            holder.selected.setChecked(dataList.get(position).get("selected").equals("1"));
            holder.imported.setVisibility(View.GONE);
        }else{
            holder.selected.setVisibility(View.GONE);
            holder.imported.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public final class ViewHolder{
        private TextView title;
        private TextView comment;
        private CheckBox selected;
        private TextView imported;
    }

    /**
     * 选中/反选所有书籍
     * @param index 为1时选中所有，0时反选所有
     * @return 所有书籍被选中时返回1，否则返回0
     */
    public int selectAllItems(int index){
        if(dataList.isEmpty()){
            return 0;
        }
        if(index == 1) {
            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).put("selected", "1");
            }
            notifyDataSetChanged();
            return 1;
        }else{
            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).put("selected", "0");
            }
            notifyDataSetChanged();
            return 0;
        }
    }

    /**
     * 根据下标将某本电子书选中/反选，并返回是否所有的书都已被选中
     * @param position 电子书下标
     * @return 当所有的书都被选中返回1，否则为0
     */
    public int setItemSelected(int position){
        if(dataList.isEmpty()){
            return 0;
        }
        boolean flag = dataList.get(position).get("selected").equals("1");
        dataList.get(position).put("selected",flag ? "0" : "1");
        notifyDataSetChanged();
        if(getSelectedItemCount() < dataList.size() - 1){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 获取到当前已被选中的电子书数目
     * @return 被选中的电子书数目(除去已经被导入的书籍)
     */
    public int getSelectedItemCount(){
        if(dataList.isEmpty()){
            return 0;
        }
        int count = 0;
        for(int i = 0;i < dataList.size();i++){
            if(dataList.get(i).get("selected").equals("1") && dataList.get(i).get("imported").equals("false")){
                count ++;
            }
        }
        return count;
    }

    public int getUsefulBookCount(){
        if(dataList.isEmpty()){
            return 0;
        }
        int count = 0;
        for(int i = 0;i < dataList.size();i++){
            if(dataList.get(i).get("imported").equals("true")){
                count ++;
            }
        }
        return getCount() - count;
    }

}
