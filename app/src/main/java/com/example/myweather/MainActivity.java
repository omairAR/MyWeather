package com.example.myweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;

    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();

            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            Log.i("button", encodedCityName);
            String result;

            result = task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22").get();
            Log.i("JSON", result);


            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=findViewById(R.id.editText);
        textView=findViewById(R.id.textView2);

    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = " ";
            URL url=null;
            HttpURLConnection connection =null;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result+=current;
                    data=reader.read();

                }

                return result;


            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could Not Find The Weather :(",Toast.LENGTH_SHORT).show();
                return "failed";
            }

        }


        @Override
        protected void onPostExecute(String s) { //s refers to upper string result
            super.onPostExecute(s);

            Log.i("JSOn", s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = "[" +jsonObject.getString("main")+ "]";


                JSONArray array = new JSONArray(weatherInfo);


                Log.i("weather content", weatherInfo);
                String message = "";

                String temp =null;
                String humidity =null;
                String temp_min =null;
                String temp_max =null;

                JSONObject jsonPart=null;

                for(int i=0; i< array.length(); i++){
                    jsonPart = array.getJSONObject(i);


                    Log.i("temp", jsonPart.getString("temp"));
                    Log.i("humidity", jsonPart.getString("humidity"));
                    Log.i("temp_min", jsonPart.getString("temp_min"));
                    Log.i("temp_max", jsonPart.getString("temp_max"));

                }
                temp="Temperature : "+jsonPart.getString("temp");
                humidity="Humidity : "+jsonPart.getString("humidity");
                temp_min="Minimum : "+jsonPart.getString("temp_min");
                temp_max="Maximum : "+jsonPart.getString("temp_max");

                message=temp+ " °C \n" +humidity+ " % \n" + temp_min + " °C \n" +temp_max+ " °C";
                if(!message.equals("")){
                    textView.setText(message);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Something Went wrong :(",Toast.LENGTH_SHORT).show();
                }


            }catch (Exception e){
                e.printStackTrace();
                Log.i("cant convert", "json");
                Toast.makeText(getApplicationContext(),"City Not Found :(",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
