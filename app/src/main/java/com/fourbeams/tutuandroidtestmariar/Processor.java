package com.fourbeams.tutuandroidtestmariar;

import com.fourbeams.tutuandroidtestmariar.pojo.AllStations;
import com.fourbeams.tutuandroidtestmariar.pojo.CitiesFrom;
import com.fourbeams.tutuandroidtestmariar.pojo.CitiesTo;
import com.fourbeams.tutuandroidtestmariar.pojo.Station;
import com.fourbeams.tutuandroidtestmariar.pojo.Station_;

import java.io.IOException;
import retrofit2.Call;

/**
 * Класс запускает {@link RESTService} для получения данных с сервера.
 * </br> Далее записывает полученные данные в базу данных, используя {@link StationsDbAdapter}.
 *
 */
public class Processor {

    /**
     * Метод запускает {@link RESTService} для получения данных с сервера.
     * @exception RuntimeException в случае недоступности сервера или данных на нем.
     *
     */
    void startGetProcessor(StationsDbAdapter mDbHelper){
        try {
            getStationList(mDbHelper);
        } catch (IOException e) {
            e.printStackTrace();

            // TODO: Необходимо сделать периодическую проверку доступности данных и собирать APK с предустановленным набором данных. В текущей реализации, если сервер или данные на сервере не доступны, то выбрасывается RuntimeException.

            throw new RuntimeException();
        }
    }

    private void getStationList(StationsDbAdapter mDbHelper) throws IOException {
        // Create a REST adapter which points API endpoint
        RESTService restService = RESTServiceGenerator.createService(RESTService.class);
        // Fetch a list of the station parameters
        final Call<AllStations> call = restService.getJSON();
        // call.execute() method throws exception if API not available or the timeout values are reached
        AllStations stationsList = call.execute().body();
        Integer stId;
        String stationTitle, countryTitle, regionTitle, districtTitle, cityTitle;
        int id = 1;
        for (Object cityFrom : stationsList.getCitiesFrom()) {
            CitiesFrom cF = (CitiesFrom) cityFrom;
            countryTitle = cF.getCountryTitle();
            regionTitle = cF.getRegionTitle();
            districtTitle = cF.getDistrictTitle();
            cityTitle = cF.getCityTitle();
            for (Object station : cF.getStations()) {
                stId = id;
                Station st = (Station) station;
                stationTitle = st.getStationTitle();
                mDbHelper.createStationFrom(stId, stationTitle, countryTitle, regionTitle, districtTitle, cityTitle);
                id++;
            }
        }
        id = 1;
        for (Object cityTo : stationsList.getCitiesTo()) {
            CitiesTo cT = (CitiesTo) cityTo;
            countryTitle = cT.getCountryTitle();
            regionTitle = cT.getRegionTitle();
            districtTitle = cT.getDistrictTitle();
            cityTitle = cT.getCityTitle();
            for (Object station : cT.getStations()) {
                stId = id;
                Station_ st = (Station_) station;
                stationTitle = st.getStationTitle();
                mDbHelper.createStationTo(stId, stationTitle, countryTitle, regionTitle, districtTitle, cityTitle);
                id++;
            }
        }
    }
}
