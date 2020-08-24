package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsURLs = new ArrayList<String>();
    ArrayList<String> celebsNames = new ArrayList<String>();
    int chosenCeleb = 0;
    String [] answer = new String[4];
    int locationOfCorrectAnswer = 0;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext() , "Correct" , Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext() , "Incorrect" +celebsNames.get(chosenCeleb) , Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }



    public class ImageDownloader extends  AsyncTask<String , Void , Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return  myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {
            String results = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    results += current;
                    data = reader.read();
                }
                return results;

            } catch(Exception e){
                e.printStackTrace();
                return null;

            }

        }
    }

    public void newQuestion(){
        try {
            Random rand = new Random();

            chosenCeleb = rand.nextInt(celebsURLs.size());

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap celebImage = imageTask.execute(celebsURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = rand.nextInt(4);

            int incorrectAnswerLocation;

            for (int i=0 ; i<=4 ; i++){
                if(i == locationOfCorrectAnswer){
                    answer[i]=celebsNames.get(chosenCeleb);
                }
                else{
                    incorrectAnswerLocation = rand.nextInt(celebsURLs.size());

                    while(incorrectAnswerLocation == chosenCeleb){
                        incorrectAnswerLocation = rand.nextInt(celebsURLs.size());
                    }
                    answer [i] =celebsNames.get(incorrectAnswerLocation);
                }
            }

            button0.setText(answer[0]);
            button1.setText(answer[1]);
            button2.setText(answer[2]);
            button3.setText(answer[3]);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;
        imageView = findViewById(R.id.imageView);
        try {
            result = task.execute("https://www.imdb.com/list/ls052283250/").get();
            Log.i("Content of url" ,result);

            String[] splitResult = result.split("<div class=\"lister-list\">");
            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebsURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"height");
            m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebsNames.add(m.group(1));
            }

            newQuestion();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}