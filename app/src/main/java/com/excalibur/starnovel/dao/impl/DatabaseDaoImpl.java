package com.excalibur.starnovel.dao.impl;

import com.excalibur.starnovel.bean.Book;
import com.excalibur.starnovel.dao.DatabaseDao;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Excalibur on 2016/11/24.
 */
public class DatabaseDaoImpl implements DatabaseDao {


    @Override
    public boolean addBook(Book book) {
        book.save();
        return true;
    }

    @Override
    public boolean removeBook(String path) {
        DataSupport.deleteAll(Book.class,"path= ?",path);
        return true;
    }

    @Override
    public boolean updateBook(Book book) {
        book.save();
        return true;
    }

    @Override
    public boolean isBookInSQLite(String path) {
        return getBookByPath(path) != null;
    }

    @Override
    public List<Book> getAllBooksFromSQLite() {
        return DataSupport.order("lastReadTime").find(Book.class);
    }

    @Override
    public Book getBookByPath(String path) {
        List<Book> list = DataSupport.where("path = ?",path).find(Book.class);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public int getTotalBookNumber() {
        return DataSupport.count(Book.class);
    }

    @Override
    public int getLastedReadBookNumber() {
        List<Book> list = DataSupport.where("addTime>?","-1").find(Book.class);
        if(list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public Book getLastebBook(int lastIndex) {
        List<Book> list = DataSupport.order("lastReadTime").where("addTime>?","-1").find(Book.class);
        if(list != null){
            if(!list.isEmpty()){
                //int position = list.size() - lastIndex;
                return list.get(list.size()-1);
            }
        }
        return null;
    }
}
