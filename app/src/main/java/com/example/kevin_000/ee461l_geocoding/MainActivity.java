package com.example.kevin_000.ee461l_geocoding;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;

    Button btnShowCoord;
    EditText edtAddress;
    TextView txtCoord;

    String latitude;
    String longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowCoord = (Button)findViewById(R.id.btnShowCoordinates);
        edtAddress = (EditText)findViewById(R.id.edtAddress);
        txtCoord = (TextView)findViewById(R.id.txtCoordinates);

        btnShowCoord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new GetCoordiantes().execute(edtAddress.getText().toString().replace(" ", "+"));
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private class GetCoordiantes extends AsyncTask<String,Void,String> {

        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected  void onPreExecute()
        {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s", address);
                response = http.getHttpData(url);
                return response;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location").get("lat").toString();

                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                latitude = lat;
                longitude = lng;

                txtCoord.setText(String.format("Coordinates: %s, %s", lat, lng));

                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
    }
}
