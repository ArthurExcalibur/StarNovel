package com.excalibur.starnovel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.excalibur.starnovel.adapter.ImportListViewAdapter;
import com.excalibur.starnovel.application.NovelApplication;
import com.excalibur.starnovel.bean.Book;
import com.excalibur.starnovel.dao.impl.DatabaseDaoImpl;
import com.excalibur.starnovel.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Excalibur on 2016/11/18.
 *
 * 添加本地书籍时:
 *  name为截取的书名,
 *  path为本地路径,
 *  percent为0,
 *  lastReadTime为系统当前时间,
 *  addTime为-1,
 *  img为"imgpath"(无用),
 *  type为0,
 *  byteNumber为文件总字节长度
 * 从数据库中查询所有书籍时,排序的优先级addTime大于lastReadTime
 */
public class ImportBookActivity extends BaseActivity implements View.OnClickListener{

    private ImportListViewAdapter adapter;

    private ListView listView;
    //页面上方的全选/反选按钮
    private TextView selectAll;
    //页面中央用于展示搜寻电子书进度的Layout和TextView
    private LinearLayout infosLayout;
    private TextView infoView;
    //页面中央显示未查询到电子书的Layout和去书城的TextView
    private LinearLayout noBookLayout;
    private TextView marketTextView;

    public static final int SEARCH_START = 0;
    public static final int SEARCH_LOADING = 1;
    public static final int SEARCH_FINISHED = 2;
    public static final int SEARCH_ERROR = 3;
    public static final int IMPORT_BOOK_START = 4;
    public static final int IMPORT_BOOK_LOADING = 5;
    public static final int IMPORT_BOOK_SUCCESS = 6;
    public static final int IMPORT_BOOK_ERROR = 7;

    private List<Map<String,String>> dataList;
    private int count = 0;

    private CardView importSelected;
    private TextView importText;

    private DatabaseDaoImpl dao;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEARCH_START:{
                    noBookLayout.setVisibility(View.GONE);
                    infosLayout.setVisibility(View.VISIBLE);
                    break;
                }
                case SEARCH_LOADING:
                    infoView.setText("已查询到 : " + count + "本电子书");
                    break;
                case SEARCH_FINISHED:{
                    infosLayout.setVisibility(View.GONE);
                    adapter = new ImportListViewAdapter(ImportBookActivity.this,dataList);
                    listView.setAdapter(adapter);
                    break;
                }
                case SEARCH_ERROR:{
                    infosLayout.setVisibility(View.GONE);
                    noBookLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(ImportBookActivity.this,"没有查询到指定规格的电子书",Toast.LENGTH_SHORT).show();
                    break;
                }
                case IMPORT_BOOK_START:{
                    infosLayout.setVisibility(View.VISIBLE);
                    infoView.setVisibility(View.GONE);
                    break;
                }
                case IMPORT_BOOK_LOADING:{
                    break;
                }
                case IMPORT_BOOK_SUCCESS:{
                    infosLayout.setVisibility(View.GONE);
                    changeImportTextView();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ImportBookActivity.this,"导入成功",Toast.LENGTH_SHORT).show();
                    break;
                }

                case IMPORT_BOOK_ERROR:{
                    infosLayout.setVisibility(View.GONE);
                    changeImportTextView();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ImportBookActivity.this,"导入失败,请重新导入",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        dataList = new ArrayList<>();

        dao = new DatabaseDaoImpl();
        initViews();

        handler.sendEmptyMessage(SEARCH_START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dir = System.getenv("SECONDARY_STORAGE");
                getFileListFromLocal(new File(dir),".txt");
                //getFileListFromLocal(Environment.getExternalStorageDirectory(),".txt");
                if(dataList.isEmpty()){
                    handler.sendEmptyMessage(SEARCH_ERROR);
                }else{
                    handler.sendEmptyMessage(SEARCH_FINISHED);
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews(){
        ImageView back = (ImageView) findViewById(R.id.activity_import_back);
        back.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.activity_import_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.dataList.get(position).get("imported").equals("true")){
                    toReadActivity(dataList.get(position).get("path"));
                }else{
                    adapter.setItemSelected(position);
                    changeImportTextView();
                }
            }
        });

        selectAll = (TextView) findViewById(R.id.activity_import_selectAll);
        selectAll.setOnClickListener(this);

        importSelected = (CardView) findViewById(R.id.activity_import_importSelected);
        importSelected.setOnClickListener(this);
        importText = (TextView) findViewById(R.id.activity_import_text);

        infosLayout = (LinearLayout) findViewById(R.id.activity_import_infosLayout);
        infoView = (TextView) findViewById(R.id.activity_import_infosText);
        noBookLayout = (LinearLayout) findViewById(R.id.activity_import_noBookLayout);
        marketTextView = (TextView) findViewById(R.id.activity_import_noBookChange);
        marketTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_import_back:
                ImportBookActivity.this.finish();
                break;
            case R.id.activity_import_selectAll:
                selectAll();
                break;
            case R.id.activity_import_importSelected:
                importBooks();
                break;
            case R.id.activity_import_noBookChange:
                break;
        }
    }

    public void selectAll(){
        if(adapter == null){
            return ;
        }
        if(selectAll.getText().toString().equals("全选")){
            adapter.selectAllItems(1);
        }else{
            adapter.selectAllItems(0);
        }
        changeImportTextView();
    }

    public void changeImportTextView(){
        int count = adapter.getSelectedItemCount();
        if(count > 0) {
            importSelected.setAlpha(1);
            importText.setText("导入所选(" + count + ")");
        }else{
            importSelected.setAlpha(0.4f);
            importText.setText("导入所选");
        }
        if(count == adapter.getUsefulBookCount()){
            selectAll.setText("取消全选");
        }else{
            selectAll.setText("全选");
        }
    }

    private void importBooks(){
        if(importSelected.getAlpha() != 1){
            return ;
        }
        handler.sendEmptyMessage(IMPORT_BOOK_START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i < adapter.dataList.size();i++){
                    if(adapter.dataList.get(i).get("selected").equals("1") && adapter.dataList.get(i).get("imported").equals("false")){
                        Book book = new Book();
                        book.setName(adapter.dataList.get(i).get("title"));
                        book.setPath(adapter.dataList.get(i).get("path"));
                        book.setPercent(0);
                        book.setLastReadTime(System.currentTimeMillis() + "");
                        book.setAddTime(-1);
                        book.setImg("imgpath");
                        book.setType(0);
                        book.setByteNumber(FileUtils.getFileTotalByteNumber(adapter.dataList.get(i).get("path")));
                        boolean flag = dao.addBook(book);
                        if(flag){
                            handler.sendEmptyMessage(IMPORT_BOOK_LOADING);
                            NovelApplication.allBoosInSQLite.add(book);
                            adapter.dataList.get(i).put("imported","true");
                        }else{
                            handler.sendEmptyMessage(IMPORT_BOOK_ERROR);
                        }
                    }
                }
                handler.sendEmptyMessage(IMPORT_BOOK_SUCCESS);
            }
        }).start();
    }

    public void toReadActivity(String path){
        Intent intent = new Intent(this,ReadActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
    }

    /**
     * 根据所给的路径和文件后缀查找所有该后缀的文件
     * @param dirFile 查询路径
     * @param pix 所查询的文件后缀名
     * @return 所有符合要求的文件路径集合
     */
    public void getFileListFromLocal(File dirFile, String pix){

        Log.e("ImportBookActivity",dirFile.getAbsolutePath());
        File[] listFile = dirFile.listFiles();
        if(listFile != null){
            for (int i = 0;i < listFile.length;i++){
                File numberFile = listFile[i];
                if(numberFile.isDirectory()){
                    getFileListFromLocal(numberFile,pix);
                }else{
                    if(numberFile.getName().endsWith(pix)){
                        Map<String,String> map = new HashMap<>();
                        map.put("path",numberFile.getAbsolutePath().toString());
                        map.put("title",numberFile.getAbsolutePath().substring(numberFile.getAbsolutePath().lastIndexOf("/") + 1,numberFile.getAbsolutePath().indexOf(".txt")));
                        map.put("comment",getFileSize(numberFile));
                        map.put("selected","0");
                        map.put("imported",String.valueOf(dao.isBookInSQLite(numberFile.getAbsolutePath().toString())));
                        dataList.add(map);
                        count++;
                        handler.sendEmptyMessage(SEARCH_LOADING);
                    }
                }
            }
        }
    }

    public String getFileSize(File file){
        DecimalFormat df = new DecimalFormat("#.00");
        String result = "";
        long byteNumber;
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            byteNumber = is.available();

            if(byteNumber < 1024){
                result = df.format((double)byteNumber) + "B";
            }else if(byteNumber < 1024 * 1024){
                result = df.format((double)byteNumber / 1024) + "K";
            }
            else if(byteNumber < 1024 * 1024 * 1024){
                result = df.format((double)byteNumber / (1024 * 1024)) + "M";
            }
            else{
                result = df.format((double)byteNumber / (1024 * 1024 * 1024)) + "G";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
