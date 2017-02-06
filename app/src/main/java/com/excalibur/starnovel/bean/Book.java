package com.excalibur.starnovel.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/1/5.
 */
public class Book extends DataSupport{

    private int id;
    private String name;//书名
    private String img;//封面路径，当书籍类型为本地书籍时可为空
    private String path;//书籍路径(本地或网址)
    private long percent;//阅读进度
    private long byteNumber;//书籍总长度
    private int addTime;//添加时间，默认为-1(此变量用来筛选最近阅读的书籍)
    private String lastReadTime;//最后一次阅读时间，默认为导入时间
    private int type;//书籍类型，0表示本地书籍，10表示网络连载，11表示网络完结

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getPath() {
        return path;
    }

    public long getPercent() {
        return percent;
    }

    public long getByteNumber() {
        return byteNumber;
    }

    public int getAddTime() {
        return addTime;
    }

    public String getLastReadTime() {
        return lastReadTime;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setByteNumber(long byteNumber) {
        this.byteNumber = byteNumber;
    }

    public void setPercent(long percent) {
        this.percent = percent;
    }

    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }

    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public void setType(int type) {
        this.type = type;
    }

}
