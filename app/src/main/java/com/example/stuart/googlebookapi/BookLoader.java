package com.example.stuart.googlebookapi;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Stuart on 27/10/2017.
 */

public class BookLoader extends AsyncTaskLoader {

    private String mQueryString;

    public BookLoader(Context context, String queryString) {
        super(context);
        mQueryString = queryString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();    }

    @Override
    public List<Book> loadInBackground() {
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Book> books = QueryUtils.fetchBookData(getContext(), mQueryString);
        return books;
    }
}
