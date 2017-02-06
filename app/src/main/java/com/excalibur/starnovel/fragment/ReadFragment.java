package com.excalibur.starnovel.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.excalibur.starnovel.ImportBookActivity;
import com.excalibur.starnovel.MainActivity;
import com.excalibur.starnovel.R;
import com.excalibur.starnovel.ReadActivity;
import com.excalibur.starnovel.adapter.FragmentReadRecyAdapter;
import com.excalibur.starnovel.application.NovelApplication;
import com.excalibur.starnovel.bean.Book;

/**
 * Created by Administrator on 2017/1/5.
 */
public class ReadFragment extends Fragment implements View.OnClickListener{

    private View rootView;

    private FragmentReadRecyAdapter adapter;

    private View importLayout;//导入书籍按钮和提示信息

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.fra_read,container,false);
            initViews(rootView);
        }
        return rootView;
    }

    private void initViews(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fra_read_recyView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        adapter = new FragmentReadRecyAdapter(getActivity());

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_read_recy_header,null);
        TextView continueRead = (TextView) headerView.findViewById(R.id.fra_read_recy_header_continue);
        continueRead.setOnClickListener(this);
        TextView title = (TextView) headerView.findViewById(R.id.fra_read_recy_header_title);
        title.setOnClickListener(this);

        adapter.setHeaderView(headerView);
        adapter.setOnRecyItemClickedListener(new FragmentReadRecyAdapter.onRecyItemClickedListener() {
            @Override
            public void onRecyItemClicked(int position) {
                if(NovelApplication.isManageMode){
                    adapter.addSelectedItem(position + 1);
                    adapter.notifyItemChanged(position + 1);
                    ((MainActivity)getActivity()).updateManageLayout(adapter.getSelectedCount());
                }else{
                    Book b = NovelApplication.allBoosInSQLite.get(NovelApplication.allBoosInSQLite.size() - 1 - position);
                    changeActivity(ReadActivity.class, b.getPath());
                }
            }
        });
        recyclerView.setAdapter(adapter);

        importLayout = view.findViewById(R.id.fra_read_importLayout);

        Button importBtn = (Button) view.findViewById(R.id.fra_read_importBtn);
        importBtn.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        if(NovelApplication.allBoosInSQLite.isEmpty()){
            importLayout.setVisibility(View.VISIBLE);
        }else{
            importLayout.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    public void showButtomDialog(Context context){
        final Dialog dialog = new Dialog(context, R.style.ButtomActivityStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimButtom);
        View view = View.inflate(context, R.layout.activity_main_dialog_import, null);
        TextView market = (TextView) view.findViewById(R.id.activity_dialog_toMarket);
        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).change2MarketTap();
                dialog.dismiss();
            }
        });
        TextView importLocal = (TextView) view.findViewById(R.id.activity_dialog_import);
        importLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(ImportBookActivity.class,null);
                dialog.dismiss();
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
        dialog.show();
    }

    public void changeManageMode(){
        adapter.notifyDataSetChanged();
    }

    public int selectedAllItems(){
        return adapter.selectedAllItems();
    }

    public int getSelectedCount(){
        return adapter.selectedBooks.size();
    }

    public void deleteAllSelectedItems(){
        adapter.deleteAllItemSelected();
        if(NovelApplication.allBoosInSQLite.isEmpty()){
            importLayout.setVisibility(View.VISIBLE);
        }else{
            importLayout.setVisibility(View.GONE);
        }
    }

    public void clearAllSelectedItems(){
        adapter.clearAllSelectedItems();
    }

    private void changeActivity(Class<? extends Activity> activity,String extra){
        Intent intent = new Intent(getActivity(),activity);
        if(extra != null){
            intent.putExtra("path",extra);
        }
        getActivity().startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fra_read_importBtn:
                showButtomDialog(getActivity());
                break;
            case R.id.fra_read_recy_header_title:
            case R.id.fra_read_recy_header_continue:
                changeActivity(ReadActivity.class,NovelApplication.getFirstBook().getPath());
                break;
            default:
                break;
        }
    }
}
