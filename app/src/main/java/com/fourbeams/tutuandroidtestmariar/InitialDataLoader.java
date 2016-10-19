package com.fourbeams.tutuandroidtestmariar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Класс проверяет есть ли данные в базе данных и, в случае их отсутствия,
 * запускает {@link Processor#startGetProcessor(StationsDbAdapter)}для загрузки данных с сервера.
 * <br/> Во время загрузки показывает диалоговое окно с ProgressBar.
 */
public class InitialDataLoader extends AsyncTask {

    private ProgressDialog progressDialog;
    private Context context;
    private StationsDbAdapter mDbHelper;

    public InitialDataLoader(Context context){
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDbHelper = new StationsDbAdapter(context);
        progressDialog = ProgressDialog.show(context, null,
                context.getString(R.string.initial_data_loading_dialog_message), true);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressDialog.dismiss();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        mDbHelper.open();
        boolean isDbEmpty = mDbHelper.isDbEmpty();
        if (isDbEmpty) new Processor().startGetProcessor(mDbHelper);
        mDbHelper.close();
        return null;
    }
}
