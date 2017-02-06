package com.excalibur.starnovel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.excalibur.starnovel.application.NovelApplication;
import com.excalibur.starnovel.dao.impl.DatabaseDaoImpl;
import com.excalibur.starnovel.fragment.MarketFragment;
import com.excalibur.starnovel.fragment.ReadFragment;
import com.excalibur.starnovel.fragment.WriteFragment;
import com.excalibur.starnovel.utils.ActivityController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,TabHost.OnTabChangeListener{

    private String[] tabTitle = new String[]{"书架","书城","原创"};

    private int[] titleImg = new int[]{R.mipmap.bookstore,R.mipmap.bookmarket,R.mipmap.me};
    private int[] titleImgLight = new int[]{R.mipmap.bookstore_selected,R.mipmap.bookmarket_selected,R.mipmap.me_selected};

    private Toolbar toolbar;
    private FragmentTabHost fragmentTabHost;
    private ReadFragment readFragment;
    private MarketFragment marketFragment;
    private WriteFragment writeFragment;

    private LinearLayout manageLayout;
    private TextView manageCancel;
    private TextView manageDelete;
    private TextView manageDelete1;

    private long lastBackClickTime = -1;
    private DatabaseDaoImpl dao;

    private Dialog deleteBookDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dao = new DatabaseDaoImpl();
        initViews();
    }

    private void initViews() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_navigation);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
            drawerLayout.setDrawerListener(toggle);
            toggle.syncState();
        }

        List<Class<? extends Fragment>> fragmentList = new ArrayList<>();
        fragmentList.add(ReadFragment.class);
        fragmentList.add(MarketFragment.class);
        fragmentList.add(WriteFragment.class);

        fragmentTabHost = (FragmentTabHost) findViewById(R.id.activity_main_tabHost);
        fragmentTabHost.setup(MainActivity.this,getSupportFragmentManager(),R.id.activity_main_content);
        for (int i = 0; i < 3; i++) {
            TabHost.TabSpec tab = fragmentTabHost.newTabSpec(tabTitle[i]).setIndicator(getView(i));
            fragmentTabHost.addTab(tab,fragmentList.get(i),null);
        }

        fragmentTabHost.setOnTabChangedListener(this);

        TextView settingView = (TextView) findViewById(R.id.activity_main_setting);
        settingView.setOnClickListener(this);
        TextView exitView = (TextView) findViewById(R.id.activity_main_exit);
        exitView.setOnClickListener(this);

        manageLayout = (LinearLayout) findViewById(R.id.activity_main_manageLayout);
        manageCancel = (TextView) findViewById(R.id.activity_main_manage_cancel);
        manageDelete = (TextView) findViewById(R.id.activity_main_manage_delete1);
        manageDelete1 = (TextView) findViewById(R.id.activity_main_manage_delete2);
        manageCancel.setOnClickListener(this);
        manageDelete.setOnClickListener(this);
        manageDelete1.setOnClickListener(this);
    }

    private View getView(int index){
        View view = getLayoutInflater().inflate(R.layout.items_tab,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.items_tab_img);
        TextView title = (TextView) view.findViewById(R.id.items_tab_text);
        if(index == 0){
            imageView.setBackgroundResource(titleImgLight[index]);
            title.setTextColor(Color.parseColor("#2878DC"));
        }else{
            imageView.setBackgroundResource(titleImg[index]);
        }
        title.setText(tabTitle[index]);
        return view;
    }

    private void showDeleteBookDialog(){
        if(deleteBookDialog == null){
            deleteBookDialog = new Dialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_remove_book,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setView(view);
            deleteBookDialog = builder.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(NovelApplication.isManageMode){
            menu.findItem(R.id.toolbar_deleteAll).setVisible(true).setTitle(readFragment.getSelectedCount() == NovelApplication.allBoosInSQLite.size() ? "取消全选" : "全选");
            menu.findItem(R.id.toolbar_import).setVisible(false);
            menu.findItem(R.id.toolbar_manage).setVisible(false);
            menu.findItem(R.id.toolbar_search).setVisible(false);
        }else{
            menu.findItem(R.id.toolbar_deleteAll).setVisible(false);
            menu.findItem(R.id.toolbar_import).setVisible(true);
            menu.findItem(R.id.toolbar_manage).setVisible(true);
            menu.findItem(R.id.toolbar_search).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(NovelApplication.isManageMode){
            if(readFragment != null){
                readFragment.clearAllSelectedItems();
            }
            manageBook(false);
            return;
        }
        if(NovelApplication.isEditMode){
            manageEditMode(false);
            return;
        }
        if(lastBackClickTime == -1){
            lastBackClickTime = System.currentTimeMillis();
            Toast.makeText(this,"再次点击返回桌面",Toast.LENGTH_SHORT).show();
        }else{
            long time = System.currentTimeMillis();
            if(time - lastBackClickTime > 3000){
                lastBackClickTime = time;
                Toast.makeText(this,"再次点击返回桌面",Toast.LENGTH_SHORT).show();
            }else{
                moveTaskToBack(false);
            }
        }
        //super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_import:
                changeActivity(ImportBookActivity.class);
                break;
            case R.id.toolbar_search:
                if(fragmentTabHost.getCurrentTab() != 1){
                    fragmentTabHost.setCurrentTab(1);
                }
                break;
            case R.id.toolbar_manage:
                if(dao.getTotalBookNumber() == 0){
                    Toast.makeText(this,"书架上暂时没有书籍",Toast.LENGTH_SHORT).show();
                }else{
                    manageBook(true);
                }
                break;
            //case R.id.toolbar_share:
            //    Toast.makeText(this,"书籍分享",Toast.LENGTH_SHORT).show();
            //    break;
            case R.id.toolbar_deleteAll:{
                int flag = readFragment.selectedAllItems();
                updateManageLayout(flag);
                break;
            }
        }
        return true;
    }

    private void manageBook(boolean enter){
        if(enter && fragmentTabHost.getCurrentTab() != 0) {
            fragmentTabHost.setCurrentTab(0);
        }
        if(readFragment == null){
            readFragment = (ReadFragment) getSupportFragmentManager().findFragmentByTag("书架");
        }
        NovelApplication.isManageMode = enter;
        readFragment.changeManageMode();
        if(enter){
            manageLayout.setVisibility(View.VISIBLE);
            manageDelete.setVisibility(View.GONE);
            manageDelete.setText("全部删除(0)");
            manageDelete.setAlpha(0.4f);
        }else {
            manageLayout.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    public void updateManageLayout(int count){
        manageDelete1.setText("全部删除(" + count + ")");
        manageDelete1.setAlpha(count == 0 ? 0.4f : 1);
        if(manageDelete.getVisibility() == View.VISIBLE){
            manageDelete.setAlpha(count == 0 ? 0.4f : 1);
        }
        invalidateOptionsMenu();
    }

    private void manageEditMode(boolean enter){

    }

    public void change2MarketTap(){
        if(fragmentTabHost.getCurrentTab() != 1){
            fragmentTabHost.setCurrentTab(1);
        }
    }

    public void changeActivity(Class<? extends Activity> ac){
        Intent intent = new Intent(this,ac);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_main_exit:
                ActivityController.finishAll();
                break;
            case R.id.activity_main_setting:
                changeActivity(SettingActivity.class);
                break;
            case R.id.activity_main_manage_cancel:{
                if(NovelApplication.isManageMode){
                    manageBook(false);
                    readFragment.selectedAllItems();
                }
                if(NovelApplication.isEditMode){
                    manageEditMode(false);
                }
                break;
            }
            case R.id.activity_main_manage_delete1:{
                break;
            }
            case R.id.activity_main_manage_delete2:{
                if(manageDelete1.getAlpha() == 1){
                    //删除所有选中的书籍
                    if(NovelApplication.isManageMode){
                        readFragment.deleteAllSelectedItems();
                        manageBook(false);
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.nav_item1){
            Toast.makeText(MainActivity.this,"我的消息",Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.nav_item2){
            Toast.makeText(MainActivity.this,"主题换肤",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"夜间模式",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onTabChanged(String tabId) {
        TabWidget tabw = fragmentTabHost.getTabWidget();
        for(int i = 0;i < tabw.getChildCount();i++){
            View view = tabw.getChildAt(i);
            ImageView iv = (ImageView)view.findViewById(R.id.items_tab_img);
            if(i == fragmentTabHost.getCurrentTab()){
                ((TextView)view.findViewById(R.id.items_tab_text)).setTextColor(Color.parseColor("#2878DC"));
                iv.setBackgroundResource(titleImgLight[i]);
            }else{
                ((TextView)view.findViewById(R.id.items_tab_text)).setTextColor(Color.BLACK);
                iv.setBackgroundResource(titleImg[i]);
            }

        }
    }
}
