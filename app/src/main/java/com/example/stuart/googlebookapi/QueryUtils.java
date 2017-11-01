package com.example.stuart.googlebookapi;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stuart on 27/10/2017.
 */
public final class QueryUtils {

    //Log tag for the log Messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();//Base URI for the books API
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q"; //parameter for the search string
    private static final String MAX_RESULTS = "maxResults"; //parameter that limits search results
    //private static final String PRINT_TYPE = "printType"; // Parameter to filter by print type

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    private QueryUtils(){

    }

    public static String createURLLink (String queryLink){

        //Build up your query URI, limiting results to 10 items and printed books
        Uri builtUri = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, queryLink)
                .appendQueryParameter(MAX_RESULTS, "20")
                //.appendQueryParameter(PRINT_TYPE, "books")
                .build();

        queryLink = builtUri.toString();

        Log.i(LOG_TAG, queryLink);

        return queryLink;
    }

    //Query the googleBooks dataset and return a list of {@Link Book} objects
    public static List<Book> fetchBookData(Context context, String requestUrl) {

        requestUrl = createURLLink(requestUrl);

        //create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
            Log.i("jsonResponse", jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //Extract relevant fields from the JSON response and create a list of {@Link Book}s
        List<Book> books = extractFeatureFromJson(context, jsonResponse);

        //Return the list of {@link Book}

        return books;
    }

    //Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the URL is null, then return early.
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code; " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                /**Closing the input stream could throw an IOException, which is why
                 * the makeHttpRequest(URL url) method signature specifies than an IOException
                 * could be thrown.
                 */
                inputStream.close();
            }
        }

        return jsonResponse;
    }


    /**
     * Convert the {@Link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * Return a list of {@Link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(Context context, String bookJSON) {
        //If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books
        List<Book> books = new ArrayList<>();

        /** Try to parse the JSON response string. If there's a problem with the way the JSON
         * is formatted, a JSONException exception object will be thrown.
         * Catch the exception so the app doesn't crash, and print the error message to the logs.
         **/
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            /**
             * Extract the JSONArray associated with the key called "items",
             * which represents a list of books
             */
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            //For each book in the bookArray, create an {@Link Book} object
            for (int i = 0; i< bookArray.length(); i++){

                //Get a book object at position i within the list of books in the JSON list
                JSONObject currentBook = bookArray.getJSONObject(i);

                /**
                 *  For each book object, extract the JSONObject associated with the
                 *  key called "volumeInfo", this represents a list if the the information
                 *  about the book.
                 */
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                //Extracts the value for the key called "title"
                String title = volumeInfo.getString("title");

                //Extracts the description for the key called "description"
                String description = volumeInfo.getString("description");

                /**
                 * Extract the JSONArray associated with the key called "authors",
                 * which represents the list of authors
                 */
                String authors = "";
                if (volumeInfo.has("authors")) {
                    // Extract the JSONArray for the key called "authors"
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                    for (int j = 0; j < authorsArray.length(); j++) {
                        if (j == 0) {
                            authors += authorsArray.getString(j);
                        } else {
                            authors += " | " + authorsArray.getString(j);
                        }
                    }
                } else {
                    authors = context.getResources().getString(R.string.unknown);
                }



                //Extracts the value for the key called "url"
                String url = volumeInfo.getString("infoLink");

                /**Create a new {@link Book} object with the title, author and url
                 * from the JSON response.
                 **/
                Book book = new Book(title, authors, url, description);

                books.add(book);
            }

        } catch (JSONException e) {
            /** If an error is thrown when executing any of the above statements in the "try" block,
             * catch the exception here, ao the app doesn't crash. Print a log message
             * with the message from the exception.
             **/
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        //Return the list of books
        return books;
    }
}
