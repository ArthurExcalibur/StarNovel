package com.excalibur.starnovel.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.excalibur.starnovel.R;
import com.excalibur.starnovel.application.NovelApplication;
import com.excalibur.starnovel.bean.Book;
import com.excalibur.starnovel.utils.FileUtils;
import com.excalibur.starnovel.utils.ScreenUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/5.
 * 首页书架RecyclerView的Adapter
 */
public class FragmentReadRecyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater layoutInflater;

    public static final int TYPE_HEADER = 1;
    private View headerView;

    private TextView img_title;//左侧书籍名和图片
    private TextView title;     //右侧书籍名
    private TextView percent;   //右侧书籍进度
    private View tishi;

    private int perWidth;
    private int perHeight;

    public Set<Integer> selectedBooks;

    public FragmentReadRecyAdapter(Context context){
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = manager.getDefaultDisplay().getWidth();
        perWidth = (width - 6 * ScreenUtils.convertDip2Px(mContext,8)) / 3;
        perHeight = perWidth / 3 * 4;
        selectedBooks = new HashSet<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            return new RecyclerView.ViewHolder(headerView){};
        }
        View view = layoutInflater.inflate(R.layout.fra_read_items,parent,false);
        return new BookFraHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_HEADER){
            tishi.setVisibility(View.VISIBLE);
            if(!NovelApplication.allBoosInSQLite.isEmpty()){
                Book b = NovelApplication.getFirstBook();
                if(null != b){
                    tishi.setVisibility(View.GONE);
                    img_title.setText(b.getName());
                    title.setText(b.getName());
                    String perc = FileUtils.getBookPercent(b.getPercent(),b.getByteNumber());
                    percent.setText("已读 : " + perc + "%");
                }
            }
            return;
        }
        BookFraHolder hol = (BookFraHolder) holder;
        Book b = NovelApplication.allBoosInSQLite.get(NovelApplication.allBoosInSQLite.size() - getRealPosition(position) - 1);

        //统一单个条目的书籍封面宽高：宽为屏幕尺寸减去一行条目的总间隔(此处为8dp * 6对应的像素)，高为宽的4/3倍，即宽高对角线满足3、4、5的比例
        ViewGroup.LayoutParams params = hol.imgTitle.getLayoutParams();
        params.width = perWidth;
        params.height = perHeight;
        hol.imgTitle.setLayoutParams(params);

        if(b.getType() == 0){//本地书籍
            hol.imgTitle.setVisibility(View.VISIBLE);
            hol.imgTitle.setText(b.getName());
            //hol.status.setVisibility(View.GONE);
        }else{
            hol.imgTitle.setVisibility(View.GONE);
            //hol.status.setVisibility(View.VISIBLE);
        }
        hol.title.setText(b.getName());
        if(b.getAddTime() == -1){
            hol.percent.setText("未读");
        }else{
            hol.percent.setText("已读" + FileUtils.getBookPercent(b.getPercent(),b.getByteNumber()) + "%");
        }
        if(NovelApplication.isManageMode){
            hol.checkBox.setVisibility(View.VISIBLE);
            hol.checkBox.setChecked(selectedBooks.contains(position));
        }else{
            hol.checkBox.setVisibility(View.GONE);
        }
        if(listener != null){
            hol.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecyItemClicked(getRealPosition(position));
                }
            });
        }
    }

    public void addSelectedItem(int pos){
        if(selectedBooks.contains(pos)){
            selectedBooks.remove(pos);
        }else{
            selectedBooks.add(pos);
        }
    }

    /**
     * 全选/反选所有书籍
     * @return
     */
    public int selectedAllItems(){
        if(selectedBooks.size() != getItemCount() - 1){
            for (int i = 1; i < getItemCount(); i++) {
                selectedBooks.add(i);
            }
        }else{
            selectedBooks.clear();
        }
        notifyDataSetChanged();
        return selectedBooks.size();
    }

    public int getSelectedCount(){
        return selectedBooks.size();
    }

    public boolean deleteAllItemSelected(){
        boolean flag = false;
        int size = NovelApplication.allBoosInSQLite.size();
        List<Book> deleteList = new ArrayList<>();
        for (Integer i : selectedBooks) {
            int actual = size - i;
            if(actual == size - 1){
                flag = true;
            }
            deleteList.add(NovelApplication.allBoosInSQLite.get(actual));
        }
        for (Book b : deleteList) {
            NovelApplication.allBoosInSQLite.remove(b);
            DataSupport.deleteAll(Book.class,"path=?",b.getPath());
        }
        deleteList.clear();
        //selectedBooks.clear();
        clearAllSelectedItems();
        notifyDataSetChanged();
        return flag;
    }

    public void clearAllSelectedItems(){
        selectedBooks.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if(headerView != null && position == 0){
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if(headerView == null){
            return NovelApplication.allBoosInSQLite.size();
        }else{
            return NovelApplication.allBoosInSQLite.size() + 1;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        //如果布局管理器为GridManager且RecyclerView含有HeaderView,则将HeaderView占据的列数设置为GridManager的列数
        if(manager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    public void setHeaderView(View view){
        headerView = view;
        notifyItemChanged(0);

        img_title = (TextView) headerView.findViewById(R.id.fra_read_recy_header_title);
        title = (TextView) headerView.findViewById(R.id.fra_read_recy_header_bookLayoutTitle);
        percent = (TextView) headerView.findViewById(R.id.fra_read_recy_header_percent);
        tishi = headerView.findViewById(R.id.fra_read_recy_header_tishi);

        //设置HeaderView时为了界面的统一性将书籍封面大小设置的和下方条目的封面大小一致
        //如果下方条目封面尺寸太大则设为可显示的最大尺寸
        ViewGroup.LayoutParams params = img_title.getLayoutParams();
        if(perHeight > ScreenUtils.convertDip2Px(mContext,150)){
            Log.e("TestForFragment","目标太大，缩小一点");
            params.height = ScreenUtils.convertDip2Px(mContext,150);
            params.width = params.height / 4 * 3;
        }else {
            params.width = perWidth;
            params.height = perHeight;
        }
        img_title.setLayoutParams(params);
    }

    public int getRealPosition(int position){
        return headerView == null ? position : position - 1;
    }

    private onRecyItemClickedListener listener;
    public interface onRecyItemClickedListener{
        void onRecyItemClicked(int position);
    }
    public void setOnRecyItemClickedListener(onRecyItemClickedListener listener){
        this.listener = listener;
    }

    static class BookFraHolder extends RecyclerView.ViewHolder{

        TextView imgTitle;
        TextView title;
        //TextView status;
        TextView percent;
        CheckBox checkBox;

        public BookFraHolder(View itemView) {
            super(itemView);
            imgTitle = (TextView) itemView.findViewById(R.id.fra_read_items_text);
            title = (TextView) itemView.findViewById(R.id.fra_read_items_title);
            //status = (TextView) itemView.findViewById(R.id.fra_read_items_status);
            percent = (TextView) itemView.findViewById(R.id.fra_read_items_percent);
            checkBox = (CheckBox) itemView.findViewById(R.id.fra_read_items_check);
        }
    }
}
