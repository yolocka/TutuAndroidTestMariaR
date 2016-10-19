package com.fourbeams.tutuandroidtestmariar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Активити содержит поля выбора станций отправлния, прибытия и даты.
 * </br> Запускает {@link SearchResultActivity} для поиска станций.
 * </br> Инициализирует диалог выбора даты.
 * </br> Запускает {@link InitialDataLoader} для начальной загрузки данных с сервера.
 */

public class TimetableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int mYear, mMonth, mDay;
    public final static int STATION_PICKED = 1;
    public final static int STATION_NOT_PICKED = 2;
    public final static int STATION_FROM_TEXT_VIEW_PICKED = 1;
    public final static int STATION_TO_TEXT_VIEW_PICKED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadInitialData();
    }

    private void loadInitialData() {
        new InitialDataLoader(this).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == STATION_PICKED){
            final String selectedStation = data.getStringExtra("SELECTED_STATION");
            final String fromToString = data.getStringExtra("FROM_TO_CASE");
            if (fromToString.equals("from")) {
                final TextView textView = (TextView) findViewById(R.id.station_from_field);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(selectedStation);
                    }
                });
            }else if (fromToString.equals("to")){
                final TextView textView = (TextView) findViewById(R.id.station_to_field);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(selectedStation);
                    }
                });
            }
        }
        if(resultCode == STATION_NOT_PICKED) {
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.timetable_item) {
            fragmentClass = Seachable_Fields_Fragment.class;
        } else if (id == R.id.about_program) {
            fragmentClass = AboutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Вставляем фрагмент, заменяя текущий фрагмент
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_timetable, fragment).commit();
        // Выделяем выбранный пункт меню в шторке
        item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void searchFrom(View view){
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("TO_OR_FROM_CASE", "from");
        startActivityForResult(intent, STATION_FROM_TEXT_VIEW_PICKED);
    }

    public void searchTo(View view){
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("TO_OR_FROM_CASE", "to");
        startActivityForResult(intent, STATION_TO_TEXT_VIEW_PICKED);
    }

    public void callDatePicker(View view) {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        final String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        final TextView textView = (TextView) findViewById(R.id.tvDate);
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(editTextDateParam);
                            }
                        });
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
