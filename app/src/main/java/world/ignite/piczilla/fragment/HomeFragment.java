package world.ignite.piczilla.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import world.ignite.piczilla.R;


public class HomeFragment extends Fragment implements View.OnClickListener {

    // Declaring required variables.

    private static String LOG_TAG = "HOME_FRAGMENT";
    private static String KEY_INDEX = "INDEX";
    private ImageView mImage_One;
    private ImageView mImage_Two;
    private ImageView mImage_Three;
    private ImageView mImage_Four;
    private TextView mPageNumber;
    private ProgressBar mProgressBar;
    private int mNumber;
    private int mPageInteger;
    private Button mNextButton;
    private Button mPrevButton;
    private int mCurrentIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        bindViews();
        return view;
    }

    // Initializing the Fragment.

    private void initViews(View rootView) {
        mImage_One = (ImageView) rootView.findViewById(R.id.image1);
        mImage_Two = (ImageView) rootView.findViewById(R.id.image2);
        mImage_Three = (ImageView) rootView.findViewById(R.id.image3);
        mImage_Four = (ImageView) rootView.findViewById(R.id.image4);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mPageNumber = (TextView) rootView.findViewById(R.id.page_number);
        mNextButton = (Button) rootView.findViewById(R.id.btn_next);
        mPrevButton = (Button) rootView.findViewById(R.id.btn_prev);
        mNumber = 0;
        mPageInteger = 1;
        mPageNumber.setText("" + mPageInteger);
        new LoadImage().execute();

    }

    // Setting up Buttons for action.

    private void bindViews() {
        mNextButton.setOnClickListener(this);
        mPrevButton.setOnClickListener(this);
    }

    // Overriding onClick for Button Click Action.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                Log.d(LOG_TAG, "Next clicked");
                mPageInteger += 1;
                if (mPageInteger < 17) {
                    mNumber = mNumber + 4;
                    mPageNumber.setText("" + mPageInteger);
                    new LoadImage().execute();
                } else {
                    Toast.makeText(getActivity(), "Image Search Ends Here...", Toast.LENGTH_SHORT).show();
                    mPageInteger = mPageInteger - 1;
                }
                break;
            case R.id.btn_prev:
                Log.d(LOG_TAG, "Previous clicked");
                mPageInteger -= 1;
                if (mPageInteger > 0) {
                    mNumber = mNumber - 4;
                    new LoadImage().execute();
                    mPageNumber.setText("" + mPageInteger);
                } else {
                    mPageInteger = 1;
                    Toast.makeText(getActivity(), "No image before this page", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    // LoadImage class for Asynchronous Task.

    private class LoadImage extends AsyncTask<URL, Integer, ArrayList<String>> {

        //Setting Up Progress Bar Visible.

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }


        // doInBackground - Doing Background Task for opening AJAX URL and Extracting the JSON Response.

        @Override
        protected ArrayList<String> doInBackground(URL... params) {
            ArrayList<String> item = null;
            try {

                // Checking URL Connection.

                URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&start=" + mNumber);
                URLConnection connection = url.openConnection();
                String line = null;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String json = builder.toString();


                // Extracting JSON Data From URL.
                item = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(json);
                JSONObject respObject = jsonObject.getJSONObject("responseData");
                JSONArray resObjects = respObject.getJSONArray("results");

                for (int i = 0; i < resObjects.length(); i++) {
                    item.add(resObjects.getJSONObject(i).getString("unescapedUrl"));
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();

                item = null;
            } catch (IOException e) {
                e.printStackTrace();
                item = null;
            } catch (JSONException e) {
                e.printStackTrace();

                item = null;
            }
            return item;
        }


        // Loading the images from the extracted String type URL using Picasso Library.

        @Override
        protected void onPostExecute(ArrayList<String> item) {
            super.onPostExecute(item);
            if (item != null) {
                Picasso.with(getActivity()).load(item.get(0).toString()).placeholder(R.drawable.products).into(mImage_One);
                Picasso.with(getActivity()).load(item.get(1).toString()).placeholder(R.drawable.products).into(mImage_Two);
                Picasso.with(getActivity()).load(item.get(2).toString()).placeholder(R.drawable.products).into(mImage_Three);
                Picasso.with(getActivity()).load(item.get(3).toString()).placeholder(R.drawable.products).into(mImage_Four);
            } else {
                Toast.makeText(getActivity(), "Error while loading the images", Toast.LENGTH_SHORT).show();
            }

            mProgressBar.setVisibility(View.GONE);
        }
    }
}
