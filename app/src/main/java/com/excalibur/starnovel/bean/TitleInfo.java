package com.excalibur.starnovel.bean;

/**
 * Created by Administrator on 2017/1/12.
 */
public class TitleInfo {

    private int index;
    private String title;
    private int startLength;

    public TitleInfo(){

    }

    public TitleInfo(int index, String title, int startLength) {
        this.index = index;
        this.title = title;
        this.startLength = startLength;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public long getStartLength() {
        return startLength;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartLength(int startLength) {
        this.startLength = startLength;
    }
}
