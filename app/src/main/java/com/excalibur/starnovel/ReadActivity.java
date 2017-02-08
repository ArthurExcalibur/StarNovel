package com.excalibur.starnovel;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.excalibur.starnovel.adapter.BookTitlesAdapter;
import com.excalibur.starnovel.application.NovelApplication;
import com.excalibur.starnovel.bean.Book;
import com.excalibur.starnovel.parser.LocalNovelParser;
import com.excalibur.starnovel.utils.Constants;
import com.excalibur.starnovel.utils.FileUtils;
import com.excalibur.starnovel.utils.ScreenUtils;
import com.excalibur.starnovel.utils.ThemeUtils;
import com.excalibur.starnovel.view.ThemeChangeableImageView;
import com.excalibur.starnovel.view.ThemeChangeableRelativeLayout;
import com.excalibur.starnovel.view.ThemeChangeableTextView;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Excalibur on 2016/11/24.
 */
public class ReadActivity extends BaseActivity implements
        View.OnClickListener,View.OnTouchListener{

    private SharedPreferences sp;

    private DrawerLayout drawerLayout;
    private ThemeChangeableRelativeLayout drawerView;
    private ListView muluListView;

    private LinearLayout firstUseLayout;//首次使用时展示的视图
    private ThemeChangeableTextView loadingText;

    private ThemeChangeableTextView textView1;
    private ThemeChangeableRelativeLayout textView2;

    //每一章开头的标题以及整体的View
    private ThemeChangeableTextView contentTitle;
    private ThemeChangeableRelativeLayout contentView;

    //注意有通知栏和没有通知栏时viewHeight的变化
    private static int viewHeight;
    private static int titleHeight;

    public Book book;
    private String charset;
    private String path;

    private LocalNovelParser localNovelParser;

    public static final int PARSER_LOADING = 0;//正在解析目录
    public static final int PARSER_ERROE = 1;//获取内容失败或者目录解析失败
    public static final int PARSER_SUCCESS = 2;//目录解析成功

    private int screenWidth;
    private int screenHeight;

    private BookTitlesAdapter adapter;

    private String pageContent;
    private List<String> pageList;
    private int currentPageNumber;

    private ObjectAnimator pageAnim;

    private Dialog funcDialog;
    private Dialog fontDialog;
    private Dialog brightDialog;

    private ThemeChangeableTextView textSizeView;
    private SeekBar progressSeekbar;
    private SeekBar brightnessSeekbar;

    private int brightness;
    private int themeId;
    private float textSize;//单位 : dp

    public enum CHANGE{
        NEXT,
        FORMER,
        SEEK_INDEX,
        SEEK_NEXT,
        SEEK_FORMER,
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PARSER_LOADING:{
                    textView1.setText("加载中...");
                    break;
                }
                case PARSER_SUCCESS:{
                    initViewsAfterParse((String)msg.obj);
                    break;
                }
                case PARSER_ERROE:{
                    String error = (String) msg.obj;
                    Toast.makeText(ReadActivity.this,error,Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        hideStatusBar();

        sp = getSharedPreferences(Constants.SHARE_PREFS,MODE_PRIVATE);
        //从SharedPreferences中获取到上一次退出时界面主题和字体等设置
        getDataFromSharedPrefences();

        if(themeId == 1){
            setTheme(R.style.ReadTheme1);
        }else if(themeId == 2){
            setTheme(R.style.ReadTheme2);
        }else if(themeId == 3){
            setTheme(R.style.ReadTheme3);
        }else{
            setTheme(R.style.ReadTheme4);
        }

        if(brightness != getScreenBrightness()){
            changeWindowBrightness(brightness);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        initDatas();
        initViews();

        /**
         * 首次使用时展示功能菜单
         */
        //checkForFirstUse();
    }

    @Override
    protected void onPause() {
        updateBookStatus();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        localNovelParser.unbindFile();
        //updateBookStatus();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateBookStatus();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            hideStatusBar();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    private void hideStatusBar(){
        if(Build.VERSION.SDK_INT < 16){
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //hide navigationBar
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(sp.getBoolean(Constants.VOLUME_CHANGE_PAGE,true)){
            if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
                String res = getNextPageByType(CHANGE.NEXT,-1);
                if(res != null) {
                    Toast.makeText(ReadActivity.this,res,Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
                String res = getNextPageByType(CHANGE.FORMER,-1);
                if(res != null) {
                    Toast.makeText(ReadActivity.this,res,Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                drawerLayout.closeDrawers();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkForFirstUse(){
        boolean isFirstUse = sp.getBoolean(Constants.IS_FIRST_USE,true);
        if(isFirstUse){
            //如果是首次使用，显示新手向导
            firstUseLayout.setVisibility(View.VISIBLE);
            firstUseLayout.setOnClickListener(this);
        }
    }

    //在这里将设置的字体大小，主题样式等获取到
    private void getDataFromSharedPrefences(){
        textSize = sp.getFloat(Constants.TEXTSIZE,15);
        brightness = sp.getInt(Constants.BRIGHTNESS,-1);
        themeId = sp.getInt(Constants.THEMEID,1);
    }

    private void initDatas(){
        screenWidth = ScreenUtils.getScreenMetrics(this).widthPixels;
        screenHeight = ScreenUtils.getScreenMetrics(this).heightPixels;
        path = getIntent().getStringExtra("path");
        charset = FileUtils.getTextCharset(path);
        book = DataSupport.where("path=?",path).find(Book.class).get(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                localNovelParser = new LocalNovelParser(book);
                localNovelParser.parseTitleInfo();
                while (true){
                    handler.sendEmptyMessage(PARSER_LOADING);
                    if(localNovelParser.getParseOver()){
                        break;
                    }
                }
                Message message = Message.obtain();
                message.what = PARSER_SUCCESS;
                String titleInfo = localNovelParser.getCurrentContent();
                if(titleInfo != null){
                    message.obj = titleInfo;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private void initViews(){
        textView1 = (ThemeChangeableTextView) findViewById(R.id.activity_read_textview1);
        textView2 = (ThemeChangeableRelativeLayout) findViewById(R.id.activity_read_textview2);

        contentTitle = (ThemeChangeableTextView) findViewById(R.id.activity_read_contentText);
        contentView = (ThemeChangeableRelativeLayout) findViewById(R.id.activity_read_contentView);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_read_drawerLayout);
        drawerView = (ThemeChangeableRelativeLayout) findViewById(R.id.activity_read_drawerView);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ThemeChangeableTextView drawerTitle = (ThemeChangeableTextView) findViewById(R.id.activity_read_drawerTitle);
        drawerTitle.setText(book.getName());

        contentView.setOnTouchListener(this);

        firstUseLayout = (LinearLayout) findViewById(R.id.activity_read_contentFirstUse);
        loadingText = (ThemeChangeableTextView) findViewById(R.id.activity_read_loadingText);
    }

    private void initViewsAfterParse(String msg){
        muluListView = (ListView) findViewById(R.id.activity_read_mulu_listview);
        adapter = new BookTitlesAdapter(ReadActivity.this,localNovelParser.getTitleList(),localNovelParser.getCurrentIndex());
        muluListView.setAdapter(adapter);
        muluListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String res = getNextPageByType(CHANGE.SEEK_INDEX,position);
                if(res != null){
                    Toast.makeText(ReadActivity.this,res,Toast.LENGTH_SHORT).show();
                }
                if(drawerLayout.isDrawerOpen(drawerView)){
                    drawerLayout.closeDrawer(drawerView);
                }
            }
        });

        textView1.setText(msg);
        pageContent = msg;
        titleHeight = contentTitle.getHeight();
        viewHeight = screenHeight - ScreenUtils.convertDip2Px(this,70);

        initTextFont(textSize);

        int length = 0;
        for (int i = 0;i < pageList.size();i++){
            if(length + localNovelParser.getCurrentTitleInfo().getStartLength() + 1 >= book.getPercent()){
                currentPageNumber = i;
                break;
            }
            try {
                length += pageList.get(i).getBytes(charset).length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            currentPageNumber = 0;
        }
        if(currentPageNumber != 0){
            contentTitle.setVisibility(View.GONE);
        }
        textView1.setText(pageList.get(currentPageNumber));
        String title = localNovelParser.getCurrentTitleInfo().getTitle();
        if(!title.equals(book.getName())){
            contentTitle.setText(title);
        }else {
            contentTitle.setVisibility(View.GONE);
        }

        loadingText.setVisibility(View.GONE);
    }

    public void showMuluMenu(){
        if(!drawerLayout.isDrawerOpen(drawerView)){
            drawerLayout.openDrawer(drawerView);

            int index = localNovelParser.getCurrentIndex();// - (pos / 2);
            int start = muluListView.getFirstVisiblePosition();
            int end = muluListView.getLastVisiblePosition();
            index = index - ((end - start) / 2);
            index = index > 0 ? index : 0;
            muluListView.setSelection(index);
            adapter.setItemSelected(localNovelParser.getCurrentIndex());

            if(funcDialog.isShowing()){
                funcDialog.dismiss();
            }
        }
    }

    public void showFuncDialog(){
        //if(funcDialog == null) {
        funcDialog = new Dialog(this, R.style.ReadDialogTheme);
        funcDialog.setCanceledOnTouchOutside(true);
        funcDialog.setCancelable(true);
        Window window = funcDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimButtom);

        View view = View.inflate(this, R.layout.activity_read_dialog_func, null);
        view.setBackgroundDrawable(contentView.getBackground());
        window.setContentView(view);

        //上一章、下一章与章节跳转
        ThemeChangeableTextView former = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_former);
        former.setOnClickListener(this);
        former.setTextColor(textView1.getTextColors());
        ThemeChangeableTextView next = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_next);
        next.setOnClickListener(this);
        next.setTextColor(textView1.getTextColors());

        ThemeChangeableImageView font = (ThemeChangeableImageView) view.findViewById(R.id.activity_read_font);
        font.setOnClickListener(this);
        ThemeChangeableImageView light = (ThemeChangeableImageView) view.findViewById(R.id.activity_read_light);
        light.setOnClickListener(this);
        ThemeChangeableImageView mulu = (ThemeChangeableImageView) view.findViewById(R.id.activity_read_mulu);
        mulu.setOnClickListener(this);
        ThemeChangeableImageView more = (ThemeChangeableImageView) view.findViewById(R.id.activity_read_more);
        more.setOnClickListener(this);

        progressSeekbar  = (SeekBar) view.findViewById(R.id.activity_read_seekbar);
        progressSeekbar.setMax((int) book.getByteNumber());
        progressSeekbar.setProgress((int) book.getPercent());
        progressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String content = localNovelParser.seekToByte(seekBar.getProgress());
                if(null != content){
                    int start = (int) localNovelParser.getCurrentTitleInfo().getStartLength();
                    autoSplitContent(localNovelParser.getCurrentContent(),textView1);
                    currentPageNumber = 0;
                    for (int i = 0; i < pageList.size(); i++) {
                        try {
                            int length = pageList.get(i).getBytes(charset).length;
                            if(start + length >= seekBar.getProgress()){
                                currentPageNumber = i;
                                break;
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    String title = localNovelParser.getCurrentTitleInfo().getTitle();
                    if(currentPageNumber == 0 && !title.equals(book.getName())){
                        contentTitle.setText(title);
                        contentTitle.setVisibility(View.VISIBLE);
                    }else{
                        contentTitle.setVisibility(View.GONE);
                    }
                    textView1.setText(pageList.get(currentPageNumber));
                    updateBookPercent();
                }
            }
        });
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //}
        if(drawerLayout.isDrawerOpen(drawerView)){
            drawerLayout.closeDrawer(drawerView);
        }
        funcDialog.show();
    }

    public void showFontDialog(){
        //if(fontDialog == null){
        fontDialog = new Dialog(this,R.style.ButtomActivityStyle);
        fontDialog.setCanceledOnTouchOutside(true);
        fontDialog.setCancelable(true);

        Window window = fontDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimButtom);

        View view = View.inflate(this,R.layout.activity_read_dialog_fonts,null);
        view.setBackgroundDrawable(contentView.getBackground());

        ThemeChangeableTextView fontNumber = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_fontNumber);
        fontNumber.setTextColor(textView1.getTextColors());
        ThemeChangeableTextView themeNumber = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_themeNumber);
        themeNumber.setTextColor(textView1.getTextColors());

        ThemeChangeableTextView add = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_font_add);
        add.setOnClickListener(this);
        add.setTextColor(textView1.getTextColors());
        ThemeChangeableTextView less = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_font_less);
        less.setOnClickListener(this);
        less.setTextColor(textView1.getTextColors());

        textSizeView = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_font_size);
        textSizeView.setText(textSize + "");
        textSizeView.setTextColor(textView1.getTextColors());

        ThemeChangeableTextView theme1 = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_theme1);
        theme1.setOnClickListener(this);
        theme1.setTextColor(textView1.getTextColors());
        ThemeChangeableTextView theme2 = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_theme2);
        theme2.setOnClickListener(this);
        theme2.setTextColor(textView1.getTextColors());
        ThemeChangeableTextView theme3 = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_theme3);
        theme3.setOnClickListener(this);
        theme3.setTextColor(textView1.getTextColors());
        ThemeChangeableTextView theme4 = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_theme4);
        theme4.setOnClickListener(this);
        theme4.setTextColor(textView1.getTextColors());

        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //}
        fontDialog.show();
    }

    public void showBrightDialog(){
        //if(brightDialog == null){
            brightDialog = new Dialog(this,R.style.ButtomActivityStyle);
            brightDialog.setCanceledOnTouchOutside(true);
            brightDialog.setCancelable(true);

            Window window = brightDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.AnimButtom);

            View view = View.inflate(this,R.layout.activity_read_dialog_birght,null);
        view.setBackgroundDrawable(contentView.getBackground());

        ThemeChangeableTextView brightNumber = (ThemeChangeableTextView) view.findViewById(R.id.activity_read_brightnessNumber);
        brightNumber.setTextColor(textView1.getTextColors());

            brightnessSeekbar = (SeekBar) view.findViewById(R.id.activity_read_brightness_seekbar);
            brightnessSeekbar.setMax(255);
            brightnessSeekbar.setProgress(brightness);
            brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeWindowBrightness(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    brightness = seekBar.getProgress();
                    changeWindowBrightness(seekBar.getProgress());
                }
            });

            window.setContentView(view);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //}
        brightDialog.show();
    }

    public void toMoreSettingActivity(){
        Intent intent = new Intent(this,MoreSettingActivity.class);
        startActivity(intent);
        funcDialog.dismiss();
    }

    public int getScreenBrightness(){
        int brightness = 0;
        try {
            brightness = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    public void changeWindowBrightness(int brightness){
        boolean flag = false;

        //try {
        //    flag = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
       // } catch (Settings.SettingNotFoundException e) {
       //     e.printStackTrace();
       // }

       // if(flag){
       //     //如果自动调节亮度是打开的，则关闭它
       //     Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
       // }

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        getWindow().setAttributes(lp);

        sp.edit().putInt(Constants.BRIGHTNESS,brightness).commit();

        /**
         * 以下步骤是设置整个应用的屏幕亮度
         * Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
         * android.provider.Settings.System.putInt(getContentResolver(),"screen_brightness",brightness);
         * getContentResolver().notifyChange(uri,null);
         */
    }

    public void changeViewThemes(int themeId,int theme){
        setTheme(themeId);
        ThemeUtils.changeTheme(drawerLayout,getTheme());
        if(this.themeId != theme){
            sp.edit().putInt(Constants.THEMEID,theme).commit();
            this.themeId = theme;
        }
    }

    /**
     * 每次翻页之后都要及时更新观看进度
     * 有以下几种情况需要更新进度：  1.上一页/下一页
     *                          2.跳转到指定章节
     *                          3.上一章/下一章
     *                          4.跳转到指定byteNumber
     */
    public void updateBookPercent(){
        long byteNumber = localNovelParser.getCurrentTitleInfo().getStartLength();
        for (int i = 0;i < currentPageNumber;i++){
            try {
                byteNumber += pageList.get(i).getBytes(charset).length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        book.setPercent(byteNumber);
        if(progressSeekbar != null){
            progressSeekbar.setProgress((int) byteNumber);
        }
    }

    /**
     * 此方法用于在当前活动被销毁时更新数据库中的电子书阅览进度
     */
    public void updateBookStatus(){
        book.setLastReadTime(System.currentTimeMillis() + "");
        book.setAddTime(0);
        book.save();
        NovelApplication.removeToFirst(book);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_read_font:{
                funcDialog.dismiss();
                showFontDialog();
                break;
            }
            case R.id.activity_read_light:{
                funcDialog.dismiss();
                showBrightDialog();
                break;
            }
            case R.id.activity_read_mulu:
                showMuluMenu();
                break;
            case R.id.activity_read_more:
                toMoreSettingActivity();
                break;
            case R.id.activity_read_former:{
                String res = getNextPageByType(CHANGE.SEEK_FORMER,-1);
                if(res != null){
                    Toast.makeText(ReadActivity.this,res,Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.activity_read_next: {
                String res = getNextPageByType(CHANGE.SEEK_NEXT,-1);
                if(res != null){
                    book.setPercent(book.getByteNumber());
                    Toast.makeText(ReadActivity.this,"没有下一章",Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.activity_read_font_less:{
                changeTextFont(textSize - 1);
                break;
            }
            case R.id.activity_read_font_add:{
                changeTextFont(textSize + 1);
                break;
            }
            case R.id.activity_read_theme1:{
                changeViewThemes(R.style.ReadTheme1,1);
                break;
            }
            case R.id.activity_read_theme2:{
                changeViewThemes(R.style.ReadTheme2,2);
                break;
            }
            case R.id.activity_read_theme3:{
                changeViewThemes(R.style.ReadTheme3,3);
                break;
            }
            case R.id.activity_read_theme4:{
                changeViewThemes(R.style.ReadTheme4,4);
                break;
            }
            case R.id.activity_read_textview3:{
                showFuncDialog();
                break;
            }
            case R.id.activity_read_contentFirstUse:
                firstUseLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void initTextFont(float textSize){
        float pxSize = ScreenUtils.convertSp2Px(this,textSize);
        textView1.getPaint().setTextSize(pxSize);
        textView1.setText(pageContent);
        textView1.postInvalidate();
        autoSplitContent(pageContent,textView1);
        currentPageNumber = 0;

        int length = (int) localNovelParser.getCurrentTitleInfo().getStartLength();
        for (int i = 0;i < pageList.size();i++){
            try {
                length += pageList.get(i).getBytes(charset).length;
                if(length >= book.getPercent()){
                    currentPageNumber = i;
                    break;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(currentPageNumber ==0 && !localNovelParser.getCurrentTitleInfo().getTitle().equals(book.getName())){
            contentTitle.setVisibility(View.VISIBLE);
        }else{
            contentTitle.setVisibility(View.GONE);
        }
        textView1.setText(pageList.get(currentPageNumber));
    }

    //传入与设置的一律以sp为单位便于管理
    public void changeTextFont(float newTextSize){
        if(newTextSize < 12 || newTextSize > 30){
            return;
        }
        initTextFont(newTextSize);
        textSize = newTextSize;
        textSizeView.setText(textSize + "");
        sp.edit().putFloat(Constants.TEXTSIZE,textSize).apply();
        updateBookPercent();
    }

    /**
     * 按照传入的类型和章节下标获取下一页内容
     * @param type 翻页类型(上一页或者下一页或者章节跳转)
     * @param pos 默认为-1，若翻页类型为章节跳转时则为要跳转到的章节
     * @return 翻页成功并获取到内容返回true,否则为false
     */
    public String getNextPageByType(CHANGE type,int pos){
        int index = sp.getInt(Constants.CHANGE_PAGE_MODE,0);
        switch (type){
            case NEXT: {
                if(currentPageNumber == pageList.size() - 1){
                    String newContent = localNovelParser.getNextContent();
                    if(newContent != null){
                        pageContent = newContent;
                        autoSplitContent(pageContent,textView1);
                        currentPageNumber = 0;
                        getChangeAnimByIndex(index,true);
                    }else{
                        return "没有下一章";
                    }
                }else{
                    currentPageNumber++;
                    getChangeAnimByIndex(index,true);
                }
                break;
            }
            case FORMER:{
                if(currentPageNumber == 0){
                    String newContent = localNovelParser.getFormerContnet();
                    if(newContent != null){
                        pageContent = newContent;
                        autoSplitContent(pageContent,textView1);
                        currentPageNumber = pageList.size() - 1;
                        getChangeAnimByIndex(index,false);
                    }else{
                        return "没有上一章";
                    }
                }else{
                    currentPageNumber--;
                    getChangeAnimByIndex(index,false);
                }
                break;
            }
            case SEEK_FORMER:{
                String newContent = localNovelParser.getFormerContnet();
                if(newContent != null){
                    pageContent = newContent;
                    autoSplitContent(pageContent,textView1);
                    currentPageNumber = 0;
                    getChangeAnimByIndex(index,false);
                }else{
                    return "没有上一章";
                }
                break;
            }
            case SEEK_NEXT:{
                String newContent = localNovelParser.getNextContent();
                if(newContent != null){
                    pageContent = newContent;
                    autoSplitContent(pageContent,textView1);
                    currentPageNumber = 0;
                    getChangeAnimByIndex(index,true);
                }else{
                    return "没有下一章";
                }
                break;
            }
            case SEEK_INDEX:{
                String newContent = localNovelParser.seekTo(pos);
                if(newContent != null){
                    pageContent = newContent;
                    autoSplitContent(pageContent,textView1);
                    currentPageNumber = 0;
                    getChangeAnimByIndex(index,true);
                }else{
                    return "跳转失败";
                }
                break;
            }
        }
        return null;
    }

    public void getChangeAnimByIndex(int index,boolean isNext){
        if(index == 0){
            changePageByMode0(isNext);
        }else if(index == 1){
            changePageByMode1(isNext);
        }else{
            changePageByMode3();
        }
        updateBookPercent();
    }

    public void changePageByMode0(boolean isNext){
        changePage(isNext);
    }

    public void changePageByMode1(boolean isNext){
        //仿真翻页暂未完成，用覆盖翻页代替
        changePageByMode0(isNext);
    }

    public void changePageByMode3(){
        //如果当前页面是某一章节的开头，大写显示其标题
        if(currentPageNumber == 0){
            String title = localNovelParser.getCurrentTitleInfo().getTitle();
            boolean flag = title.equals(book.getName());
            if(!flag){
                contentTitle.setVisibility(View.VISIBLE);
                contentTitle.setText(title);
            }else{
                contentTitle.setVisibility(View.GONE);
            }
        }else{
            contentTitle.setVisibility(View.GONE);
        }

        textView1.setText(pageList.get(currentPageNumber));
    }

    public void changePage(final boolean isNext){
        if(pageAnim != null && pageAnim.isRunning()){
            pageAnim.end();
        }

        contentView.setDrawingCacheEnabled(true);
        final Bitmap bitmap = Bitmap.createBitmap(contentView.getDrawingCache());

        if(bitmap == null){
            return;
        }

        contentView.setDrawingCacheEnabled(false);

        final View view = new View(this);

        if(isNext){
            textView2.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }else{
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setBackgroundDrawable(new BitmapDrawable(bitmap));
            view.setLayoutParams(params);
            drawerLayout.addView(view);
        }

        //如果当前页面是某一章节的开头，大写显示其标题
        if(currentPageNumber == 0){
            //String title = parser.getDefaultBookInfo().getTitle();
            String title = localNovelParser.getCurrentTitleInfo().getTitle();
            boolean flag = title.equals(book.getName());
            if(!flag){
                contentTitle.setVisibility(View.VISIBLE);
                contentTitle.setText(title);
            }else{
                contentTitle.setVisibility(View.GONE);
            }
        }else{
            contentTitle.setVisibility(View.GONE);
        }

        textView1.setText(pageList.get(currentPageNumber));

        if(isNext){
            pageAnim = ObjectAnimator.ofFloat(contentView,"x",screenWidth,0);
        }else{
            pageAnim = ObjectAnimator.ofFloat(view,"x",0,screenWidth);
        }

        pageAnim.setDuration(300);
        pageAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(isNext){
                }else{
                    drawerLayout.removeView(view);
                }
                if(!bitmap.isRecycled()){
                    //bitmap.recycle();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        pageAnim.start();
        //updateBookPercent();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean mode = sp.getBoolean(Constants.TOUCH_SCREEN_CHANGE_PAGE,true);
        if(event.getAction() == MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            if(x < screenWidth / 4){
                if(mode){
                    String res = getNextPageByType(CHANGE.FORMER,-1);
                    if(res != null) {
                        Toast.makeText(ReadActivity.this,res,Toast.LENGTH_SHORT).show();
                    }
                }
            }else if(x < screenWidth / 4 * 3){
                showFuncDialog();
            }else{
                if(mode){
                    String res = getNextPageByType(CHANGE.NEXT,-1);
                    if(res != null) {
                        Toast.makeText(ReadActivity.this,res,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return true;
    }

    //根据页数计算当前页能容纳的最大行数
    public int calContentCount(int pageNumber,ThemeChangeableTextView textView){
        int normalHeight = viewHeight;
        if(pageNumber == 0){
            normalHeight -= titleHeight;
        }
        int firstH = getLineHeight(0,textView);
        int otherH = getLineHeight(1,textView);

        return (normalHeight - firstH) / otherH + 1;
    }

    //计算每一行在屏幕上所占据的高度
    public int getLineHeight(int line,ThemeChangeableTextView textView){
        Rect rect = new Rect();
        textView.getLineBounds(line,rect);
        return rect.bottom - rect.top;
    }

    /**
     * 将一个章节的内容分页
     * @param content 章节内容
     * @param textView 用来显示章节内容的TextView
     */
    public void autoSplitContent(String content,ThemeChangeableTextView textView){
        if(pageList == null){
            pageList = new ArrayList<>();
        }
        if(!pageList.isEmpty()){
            pageList.clear();
        }

        int cellCount = 0;
        Paint paint = textView.getPaint();
        float width = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
        StringBuilder builder = new StringBuilder();
        int pageHeight = calContentCount(0,textView);
        int maxHeight = calContentCount(1,textView);

        //如果是第一章之前的内容，则其第一页最大行数不变
        if(localNovelParser.getCurrentIndex() == 0){
            pageHeight = maxHeight;
        }

        String title = localNovelParser.getCurrentTitleInfo().getTitle();
        String[] pages = content.replaceAll("\r","").split("\n");

        for(String value : pages){
            if(TextUtils.isEmpty(value)){

            }else{
                value = value.trim();
                if(TextUtils.isEmpty(value)){

                }else{
                    if(value.equals(title)){
                        continue;
                    }
                    value = "    " + value;

                    if(paint.measureText(value) <= width){
                        builder.append(value);
                        cellCount++;
                    }else{
                        float lineWidth = 0;
                        for(int i = 0;i < value.length();i++){
                            char ch = value.charAt(i);
                            lineWidth += paint.measureText(String.valueOf(ch));
                            if(lineWidth <= width){
                                builder.append(ch);
                            }else{
                                lineWidth = 0;
                                i--;
                                builder.append("\n");
                                cellCount++;
                                if(cellCount == pageHeight){
                                    pageList.add(builder.toString());
                                    builder = new StringBuilder();
                                    cellCount = 0;
                                    pageHeight = maxHeight;
                                }
                            }
                        }
                    }

                    builder.append("\n");
                    cellCount++;

                    if(cellCount < pageHeight - 1){
                        builder.append("\n");
                        cellCount ++;
                    }else if(cellCount == pageHeight - 1){
                        builder.append("\n");
                    }else{
                        pageList.add(builder.toString());
                        builder = new StringBuilder();
                        cellCount = 0;
                        pageHeight = maxHeight;
                    }
                }
            }
        }

        if(builder != null && !TextUtils.isEmpty(builder)){
            String c = builder.toString();
            pageList.add(builder.toString());
        }
    }
}
