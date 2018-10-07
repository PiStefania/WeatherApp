package patsou.stefania.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String URL_API = "your api";
    private JSONObject jsonObj;
    @BindView(R.id.grades_text_view) TextView gradesView;
    @BindView(R.id.weather_image_view) ImageView stateView;
    @BindView(R.id.city_edit_text) EditText editText;
    @BindView(R.id.search_button) ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Check if Internet is working
        if (!isNetworkAvailable(this)) {
            gradesView.setText(R.string.no_internet);
            String backgroundImage = getDrawablePath(R.drawable.internet);
            //load image
            Glide.with(getApplicationContext()).load(backgroundImage).centerCrop().crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
                            System.out.println(e.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(stateView);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = editText.getText().toString().trim();
                String fullUrl = URLBuilder(city);
                // make HTTP request to retrieve the weather
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        fullUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            jsonObj = (JSONObject) response.getJSONArray("weather").get(0);
                            if(jsonObj == null){
                                gradesView.setText(R.string.no_city_provided);
                                String backgroundImage = getDrawablePath(R.drawable.internet);
                                //load image
                                Glide.with(getApplicationContext()).load(backgroundImage).centerCrop().crossFade()
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
                                                System.out.println(e.toString());
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                return false;
                                            }
                                        })
                                        .into(stateView);
                            }else {
                                // display the grades
                                gradesView.setText(String.format("%s °C", response.getJSONObject("main").getString("temp")));

                                String backgroundImage = "";

                                //choose the image to set as background according to weather condition
                                switch (jsonObj.getString("main")) {
                                    case "Sunny":
                                        backgroundImage = getDrawablePath(R.drawable.sunny);
                                        break;
                                    case "Drizzle":
                                        backgroundImage = getDrawablePath(R.drawable.drizzle);
                                        break;
                                    case "Clouds":
                                        backgroundImage = getDrawablePath(R.drawable.cloudy);
                                        break;
                                    case "Snow":
                                        backgroundImage = getDrawablePath(R.drawable.snow);
                                        break;
                                    default:
                                        backgroundImage = getDrawablePath(R.drawable.weather);
                                        break;
                                }

                                //load image
                                Glide.with(getApplicationContext()).load(backgroundImage).centerCrop().crossFade()
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
                                                System.out.println(e.toString());
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                return false;
                                            }
                                        })
                                        .into(stateView);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                    }
                });

                AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
            }
        });
    }

    private boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private String getDrawablePath(int idDrawable) {
        @SuppressLint("DefaultLocale") Uri pathUri = Uri.parse(String.format("android.resource://patsou.stefania.weatherapp/%d", idDrawable));
        return pathUri.toString();
    }

    private String URLBuilder(String city){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("weather")
                .appendQueryParameter("q", city)
                .appendQueryParameter("appid", URL_API)
                .appendQueryParameter("units", "metric");
        return builder.build().toString();
    }
}
