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
package com.example.android.soonami;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;

/**
 * Displays information about a single earthquake.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static QuakesAdapter quakesAdapter;
    public static ArrayList<Event> eventsList = new ArrayList<>();
    public static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&endtime=2018-12-01" +
                    "&minmagnitude=6&limit=50";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        quakesAdapter = new QuakesAdapter(this, eventsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(quakesAdapter);
        quakesAdapter.notifyDataSetChanged();

        FetchData fetchData= new FetchData();
        fetchData.execute();
    }

    private class FetchData extends AsyncTask<String, Void, ArrayList<Event>> {

        String myDdata = "";
        String line = "";

        @Override
        protected ArrayList<Event> doInBackground(String... params) {
            Log.i("status","doInBackground");
            try {
                URL url = new URL(USGS_REQUEST_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(10000 /* milliseconds */);
                httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == 200) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    while(line != null){
                        line = bufferedReader.readLine();
                        myDdata = myDdata + line;
                    }

                    JSONObject jsonObject = new JSONObject(myDdata);
                    eventsList.clear();

                    JSONArray jsonArray = jsonObject.getJSONArray("features");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject firstFeature = jsonArray.getJSONObject(i);
                        JSONObject properties = firstFeature.getJSONObject("properties");

                        // Extract out the title, time, and tsunami values
                        String title = properties.getString("title");
                        long time = properties.getLong("time");
                        int tsunamiAlert = properties.getInt("tsunami");
                        eventsList.add(new Event(title, time, tsunamiAlert));
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                } else {
                    Log.e("Connection Error: ", "Error response code: " + httpURLConnection.getResponseCode());
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> result) {
            Log.i("status","onPostExecute");
            super.onPostExecute(result);
            quakesAdapter.notifyDataSetChanged();
        }
    }
}