package com.excalibur.starnovel.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Excalibur on 2017/1/13.
 */
public class Riji extends DataSupport{

    private int isSelf;
    private String image;//图片本地路径
    private String title;
    private String content;//本地文件存储目录

    public int getIsSelf(){return isSelf;}

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIsSelf(int isSelf){this.isSelf = isSelf;}
}
