package com.excalibur.starnovel.dao;

import com.excalibur.starnovel.bean.Book;

import java.util.List;

/**
 * Created by Excalibur on 2016/11/24.
 */
public interface DatabaseDao {
    boolean addBook(Book book);
    boolean removeBook(String path);
    boolean updateBook(Book book);
    boolean isBookInSQLite(String path);
    List<Book> getAllBooksFromSQLite();
    Book getBookByPath(String path);
    int getTotalBookNumber();
    int getLastedReadBookNumber();
    Book getLastebBook(int lastIndex);
}
