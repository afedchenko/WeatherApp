package com.example.weather;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private EditText editUrl;
    private Button okButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        initViews();
    }

    private void initViews() {
        webView = findViewById(R.id.web_view_browser);
        editUrl = findViewById(R.id.web_view_url);
        okButton = findViewById(R.id.web_view_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpsURLConnection urlConnection = null;
                try {
                    URL uri = new URL(editUrl.getText().toString());
                    urlConnection = (HttpsURLConnection)uri.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(10000);

                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        String result = in.lines().collect(Collectors.joining("\n"));
                        webView.loadData(result, "text/html; charset=utf-8", "utf-8");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection !=null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });
    }
}
