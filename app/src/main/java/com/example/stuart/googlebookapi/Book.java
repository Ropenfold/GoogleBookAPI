package com.example.stuart.googlebookapi;

/**
 * Created by Stuart on 27/10/2017.
 */

public class Book {

    private String mBookTitle;
    private String mBookAuthor;
    private String mBookURLLink;
    private String mBookDescription;

    public Book(String bookTitle, String bookAuthor, String bookURLLink, String bookDescription) {
        this.mBookTitle = bookTitle;
        this.mBookAuthor = bookAuthor;
        this.mBookURLLink = bookURLLink;
        this.mBookDescription = bookDescription;
     }

    public String getBookTitle() {

        return mBookTitle;
    }

    public String getBookAuthor() {

        return mBookAuthor;
    }

    public String getBookURLLink() {

        return mBookURLLink;
    }

    public String getBookDescription() {
        return mBookDescription;
    }
}