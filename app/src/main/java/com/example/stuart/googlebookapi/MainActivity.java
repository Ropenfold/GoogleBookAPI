package com.example.stuart.googlebookapi;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private String mQueryText;
    private Button mSearchButton;
    private EditText mSearchQuery;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;

    /** Adapter for the list of earthquakes */
    private BookAdapter mAdapter;


    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchQuery = (EditText) findViewById(R.id.search_text);



        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        //when the device is is rotated the view data is lost. This is because when the activity is created
        //(or recreated), the activity does not know there is a loader running.
        //An initLoader()method is needed on onCreate() of MainActivity to connect to the Loader.
        if(getLoaderManager().getLoader(BOOK_LOADER_ID) != null){
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        }


            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
         mLoadingIndicator = (ProgressBar) findViewById(R.id.loader_icon);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_search);

            mSearchButton = (Button)findViewById(R.id.searchbutton);
            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mEmptyStateTextView.setVisibility(View.GONE);

                    mQueryText = mSearchQuery.getText().toString();

                    // Get a reference to the ConnectivityManager to check state of network connectivity
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    // Get details on the currently active default data network
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    // If there is a network connection, fetch data
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Get a reference to the LoaderManager, in order to interact with loaders.
                        LoaderManager loaderManager = getLoaderManager();

                        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                        // because this activity implements the LoaderCallbacks interface).
                        loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        mLoadingIndicator.setVisibility(View.VISIBLE);

                    } else {
                        // Otherwise, display error
                        // First, hide loading indicator so error message will be visible
                        mLoadingIndicator.setVisibility(View.GONE);

                        // Update empty state with no connection error message
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                        mEmptyStateTextView.setText(R.string.no_internet_connection);

                    }
                }
            });

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Book currentBook = mAdapter.getItem(position);

                Uri bookUri = Uri.parse(currentBook.getBookURLLink());

                Intent websiteLoader = new Intent(Intent.ACTION_VIEW, bookUri);

                startActivity(websiteLoader);


            }
        });
        }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, mQueryText);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        mLoadingIndicator.setVisibility(View.GONE);

        // If there is a valid list of {@link Book}s, then dd them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }else {
            // No books found
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_books_found);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}