package com.fourbeams.tutuandroidtestmariar;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Создает REST сервис для получения данных с {@value #API_BASE_URL}.
 * <br/> Устанавливает OkHttp в качестве Http клиента и GsonConverterFactory в качестве конвертера JSON.
 * <br/> Устанавливает таймаут чтения с сервера: {@value #READ_TIMEOUT_SECONDS}
 * <br/> Устанавливает таймаут соединения с сервером: {@value #CONNECT_TIMEOUT_SECONDS}
 *
 */
public class RESTServiceGenerator {

    private static final String API_BASE_URL = "https://raw.githubusercontent.com/";
    private static final int CONNECT_TIMEOUT_SECONDS = 15;
    private static final int READ_TIMEOUT_SECONDS = 15;

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
}
