package com.excalibur.starnovel.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.excalibur.starnovel.R;
import com.excalibur.starnovel.RijiActivity;
import com.excalibur.starnovel.adapter.WriteRecyAdapter;
import com.excalibur.starnovel.bean.Riji;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Excalibur on 2017/1/13.
 */
public class WritePublicFragment extends Fragment {

    private int index;
    public static final String ARGS_PAGE = "page";

    public WritePublicFragment(){

    }

    public static WritePublicFragment newInstance(int index){
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_PAGE,index);
        WritePublicFragment fragment = new WritePublicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //index = getArguments().getInt(ARGS_PAGE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e("TestForFragment","onViewCreated...");
        List<Riji> rijiList = new ArrayList<>();
        Riji riji = new Riji();
        riji.setContent("Test Content");
        riji.setIsSelf(1);
        riji.setTitle("Test Title");
        riji.setImage("Test Image");
        rijiList.add(riji);
        Riji riji1 = new Riji();
        riji1.setContent("Test Content");
        riji1.setIsSelf(1);
        riji1.setTitle("Test Title");
        riji1.setImage(null);
        rijiList.add(riji1);
        Riji riji2 = new Riji();
        riji2.setContent("Test Content");
        riji2.setIsSelf(1);
        riji2.setTitle("Test Title");
        riji2.setImage(null);
        rijiList.add(riji2);
        Riji riji3 = new Riji();
        riji3.setContent("Test Content");
        riji3.setIsSelf(1);
        riji3.setTitle("Test Title");
        riji3.setImage("AA");
        rijiList.add(riji3);
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_write_footer,null);
        WriteRecyAdapter adapter = new WriteRecyAdapter(getActivity(),rijiList);
        adapter.addFooterView(footerView);
        adapter.setOnItemClickedListener(new WriteRecyAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position,View image) {
                Intent intent = new Intent(getActivity(), RijiActivity.class);
                if(image.getVisibility() == View.VISIBLE){
                    intent.putExtra("path","extra");
                    if(Build.VERSION.SDK_INT >= 21){
                        getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()
                                , image,"share").toBundle());
                    }else{
                        getActivity().startActivity(intent);
                    }
                }else{
                    getActivity().startActivity(intent);
                }
            }
        });
        adapter.setOnUserClickedListener(new WriteRecyAdapter.OnUserClickedListener() {
            @Override
            public void onUserClicked() {
                Toast.makeText(getActivity(),"前往个人中心",Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnShareClickedListener(new WriteRecyAdapter.OnShareClickedListener() {
            @Override
            public void onShareClicked(View v) {
                showShareMenu(v);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fra_write_public_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_write_public,container,false);
        return view;
    }

    public void showShareMenu(View v){
        PopupMenu menu = new PopupMenu(getActivity(),v);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.menu_share,menu.getMenu());
        menu.show();
    }
}
