package com.example.bingmapsdemo;

import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    private static final int MAP_WIDTH = 600;
    private static final int MAP_HEIGHT = 400;
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String API_KEY = "API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.containsKey(LATITUDE)
            && savedInstanceState.containsKey(LONGITUDE) && savedInstanceState.containsKey(API_KEY))
        {
            setEditableText(R.id.latitude_input, savedInstanceState.getString(LATITUDE));
            setEditableText(R.id.longitude_input, savedInstanceState.getString(LONGITUDE));
            setEditableText(R.id.api_key, savedInstanceState.getString(API_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString(LATITUDE, getEditableText(R.id.latitude_input));
        outState.putString(LONGITUDE, getEditableText(R.id.longitude_input));
        outState.putString(API_KEY, getEditableText(R.id.longitude_input));
    }

    /**
     * Helper procedure. Get the text content of an EditText.
     * @param id The id (as from R.id. ...) of the EditText.
     * @return The string content of the EditText.
     */
    private String getEditableText(int id)
    {
        return ((EditText) findViewById(id)).getText().toString();
    }

    /**
     * Helper procedure. Set the text content of an EditText.
     * @param id The id (as from R.id. ...) of the EditText.
     * @param value The new content of the EditText.
     */
    private void setEditableText(int id, String value)
    {
        ((EditText) findViewById(id)).setText(value);
    }

    /**
     * Helper procedure. Note that an error occurred.
     * @param errorId The id of the string error message (as from R.string. ...).
     * @param description A brief additional description.
     */
    private void noteError(int errorId, String description)
    {
        Toast.makeText(getApplicationContext(), getString(errorId) + description, Toast.LENGTH_LONG).show();
    }

    public void showLocation(final View view) {
        // Request views.
        final String latitudeStr = getEditableText(R.id.latitude_input);
        final String longitudeStr = getEditableText(R.id.longitude_input);
        final String apiKey = getEditableText(R.id.api_key);

        // Conversions.
        double tempLat = 0.0;
        double tempLong = 0.0;

        try
        {
            tempLat = Double.parseDouble(latitudeStr);
            tempLong = Double.parseDouble(longitudeStr);
        }
        catch(NumberFormatException ex)
        {
            noteError(R.string.invalid_number_input, "");
            return;
        }

        final double latitude = tempLat;
        final double longitude = tempLong;

        // On user error...
        if (Double.isNaN(latitude) || Double.isNaN(longitude))
        {
            noteError(R.string.invalid_input_warning, " inputs NaN");
            return;
        }

        MapLoader loader = new MapLoader(latitude, longitude, new MapSettings(apiKey, MAP_WIDTH, MAP_HEIGHT),
        new MapLoader.Callback()
        {
            @Override
            public void onLoaded(final Bitmap result)
            {
                view.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final ImageView mapView = ((ImageView) findViewById(R.id.map_view));
                        mapView.setImageBitmap(result);
                    }
                });
            }

            @Override
            public void onExcept(final Throwable cause)
            {
                view.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        noteError(R.string.io_exception, cause.getMessage());
                    }
                });
            }
        });

        new Thread(loader).start();
    }
}
