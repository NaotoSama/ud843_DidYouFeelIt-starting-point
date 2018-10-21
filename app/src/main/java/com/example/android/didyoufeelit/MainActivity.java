/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.didyoufeelit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.URL;

/**
 * Displays the perceived strength of a single earthquake event based on responses from people who felt the earthquake.
 */
public class MainActivity extends AppCompatActivity {

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an AsyncTask to perform the HTTP request to the given URL on a background thread.
        // Then execute the given URL
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
    }

    /**
     * Update the UI with the given earthquake information.
     */
    private void updateUi(Event earthquake) {
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(earthquake.title);

        TextView tsunamiTextView = (TextView) findViewById(R.id.number_of_people);
        tsunamiTextView.setText(getString(R.string.num_people_felt_it, earthquake.numOfPeople));

        TextView magnitudeTextView = (TextView) findViewById(R.id.perceived_magnitude);
        magnitudeTextView.setText(earthquake.perceivedStrength);
    }


    /**
     * Create an inner class called EarthquakeAsyncTask to to perform the network request on a background thread,
     * and then update the UI with the first earthquake in the response.
     */
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, Event>{

        @Override
        protected Event doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            // Perform the HTTP request for earthquake data and process the response.
            // 抓取客製的Utils.java類中的fetchEarthquakeData方法並套入網址USGS_REQUEST_URL，然後命名為earthquake並賦予客製的Event類的屬性
            // Instead of hard coding this test to only work for this specific USGS_REQUEST_URL, use the input parameter by accessing the zeroth element of the URL's array.
            // That way this EarthquakeAsyncTask can work for any string URL.
            Event result = Utils.fetchEarthquakeData(urls[0]);
            return result;
        }

        /**
         * This method is invoked on the main UI thread after the background work has been completed. We take the {@link Event} object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        @Override
        protected void onPostExecute(Event result) {  //把doInBackground方法return出來的result(屬性為自訂的Event類)帶入onPostExecute方法
            // If there is no result, do nothing.
            if (result == null) {
                return;
            }

            // Update the information displayed to the user.
            updateUi(result);  //更新UI
        }
    }
}
