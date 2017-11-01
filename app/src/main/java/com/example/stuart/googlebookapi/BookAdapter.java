package com.example.stuart.googlebookapi;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stuart on 27/10/2017.
 */

public class BookAdapter extends ArrayAdapter <Book>{

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /** Check if there is an existing list item view (called convertView) that we can use reuse,
         *otherwise, if convertView is null, then inflate a new list item layout.
         **/

        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        // Find the book at the given position in the list of eartquakes
        Book currentBook = getItem(position);

        //find the Textview with the id book_title
        TextView titleView = listItemView.findViewById(R.id.book_title);
        titleView.setText(currentBook.getBookTitle());

        //find the TextView with the id book_author
        TextView authorView = listItemView.findViewById(R.id.book_author);
        authorView.setText(currentBook.getBookAuthor().toString());

        //find the TextView with the id book_url
        TextView descriptionView = listItemView.findViewById(R.id.book_description);
        descriptionView.setText(currentBook.getBookDescription());

        return listItemView;
    }
}
