package edu.uw.fragmentdemo;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {

    private static final String TAG = "MoviesFragment";

    private ArrayAdapter<Movie> adapter;

    private OnMovieSelectedListener callback;

    private String searchTerm; //the most recent term we searched for, for persistance



    //interface supported by anyone who can respond to my clicks
    public interface OnMovieSelectedListener {
        public void movieSelected(Movie movie);
    }


    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (OnMovieSelectedListener)context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListend");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        //controller
        adapter = new ArrayAdapter<Movie>(this.getActivity(),
                R.layout.list_item, R.id.txtItem, new ArrayList<Movie>());

        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie)parent.getItemAtPosition(position);
                Log.v(TAG, "You clicked on: "+movie);

                //tell the activity to do stuff!
                //((OnMovieSelectedListener)getActivity()).movieSelected(movie);
                callback.movieSelected(movie);

            }
        });


        Log.v(TAG, "In create bundle: "+savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getString("searchTerm") != null) {
            fetchData(savedInstanceState.getString("searchTerm"));
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(searchTerm != null){ //rerun the search if we're restarted
            fetchData(searchTerm);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "savings out state");
        outState.putString("searchTerm",this.searchTerm);
    }

//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        Log.v(TAG, "In state: "+savedInstanceState);
//        if(savedInstanceState != null && savedInstanceState.getString("searchTerm") != null) {
//            fetchData(savedInstanceState.getString("searchTerm"));
//        }
//    }

    //
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Log.v(TAG, "In state: "+savedInstanceState);
//        if(savedInstanceState != null && savedInstanceState.getString("searchTerm") != null){
//            fetchData(savedInstanceState.getString("searchTerm"));
//        }
//    }

    //helper method for downloading the data via the MovieDowloadTask
    public void fetchData(String searchTerm){
        Log.v(TAG, "You searched for: "+searchTerm);
        MovieDownloadTask task = new MovieDownloadTask();
        task.execute(searchTerm);
        this.searchTerm = searchTerm;
    }

    //A task to download movie data from the internet on a background thread
    public class MovieDownloadTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            ArrayList<Movie> data = MovieDownloader.downloadMovieData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            adapter.clear();
            for(Movie movie : movies){
                adapter.add(movie);
            }
        }
    }

}
