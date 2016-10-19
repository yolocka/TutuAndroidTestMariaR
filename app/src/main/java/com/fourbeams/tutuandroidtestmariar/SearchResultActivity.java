package com.fourbeams.tutuandroidtestmariar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

/**
 * Активити предоставляет возможность выбора станции отпраления или станции прибытия.
 * </br> Также реализована возможность поиска.
 *
 */
public class SearchResultActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {

    private ListView mListView;
    private SearchView searchView;
    private StationsDbAdapter mDbHelper;
    private String toFromCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        toFromCase = getIntent().getStringExtra("TO_OR_FROM_CASE");
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        mListView = (ListView) findViewById(R.id.list);
        mDbHelper = new StationsDbAdapter(this);
        mDbHelper.open();
        showResults(null, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent emptyIntent = new Intent();
        setResult(TimetableActivity.STATION_NOT_PICKED, emptyIntent);
    }

    @Override
    public boolean onClose() {
        showResults("", false);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        showResults(query + "*", false);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        showResults(newText + "*", false);
        return false;
    }

    private void showResults(String query, Boolean isFullList) {
        Cursor cursor = null;
        if (isFullList) {cursor = mDbHelper.showStations(toFromCase);}
        else {cursor = mDbHelper.searchStation(query != null ? query.toString() : "@@@@", toFromCase);}
        if (cursor == null) {
        } else {
            // Specify the columns we want to display in the result
            String[] from = new String[] {
                    StationsDbAdapter.COL_STATION_TITLE,
                    StationsDbAdapter.COL_COUNTRY_TITLE,
                    StationsDbAdapter.COL_REGION_TITLE,
                    StationsDbAdapter.COL_DISTRICT_TITLE,
                    StationsDbAdapter.COL_CITY_TITLE};
            // Specify the Corresponding layout elements where we want the columns to go
            int[] to = new int[] {
                    R.id.sstation_title,
                    R.id.scountry_title,
                    R.id.sregion_title,
                    R.id.sdistrict_title,
                    R.id.scity_title};
            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter stations = new SimpleCursorAdapter(this,R.layout.station_info, cursor, from, to, 1);
            mListView.setAdapter(stations);
            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) mListView.getItemAtPosition(position);
                    String station = cursor.getString(cursor.getColumnIndexOrThrow(StationsDbAdapter.COL_STATION_TITLE));
                    Intent intent = new Intent();
                    intent.putExtra("SELECTED_STATION", station);
                    intent.putExtra("FROM_TO_CASE", toFromCase);
                    setResult(TimetableActivity.STATION_PICKED, intent);
                    searchView.setQuery("",true);
                    finish();
                }
            });
        }
    }
}



