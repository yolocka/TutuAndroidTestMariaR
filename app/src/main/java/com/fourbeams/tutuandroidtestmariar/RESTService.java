package com.fourbeams.tutuandroidtestmariar;

import com.fourbeams.tutuandroidtestmariar.pojo.AllStations;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Интерфейс получения списка станций.
 *
 */

public interface RESTService {
    @GET("tutu-ru/hire_android_test/master/allStations.json")
    Call<AllStations> getJSON();
}
