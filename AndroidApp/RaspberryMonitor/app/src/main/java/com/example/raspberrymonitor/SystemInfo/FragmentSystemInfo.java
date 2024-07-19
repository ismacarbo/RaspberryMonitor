package com.example.raspberrymonitor.SystemInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.raspberrymonitor.Main.MainViewModel;
import com.example.raspberrymonitor.R;

public class FragmentSystemInfo extends Fragment {
    private MainViewModel viewModel;
    private TextView temperatureTextView;
    private TextView memoryTextView;
    private TextView diskTextView;
    private TextView powerTextView;
    private WebView webView;

    public FragmentSystemInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_info, container, false);

        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        memoryTextView = view.findViewById(R.id.memoryTextView);
        diskTextView = view.findViewById(R.id.diskTextView);
        powerTextView = view.findViewById(R.id.powerTextView);
        webView = view.findViewById(R.id.webView);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Set up the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/chart.html");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("MyApplication", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
        });

        viewModel.getSystemInfo().observe(getViewLifecycleOwner(), new Observer<SystemInfoResponse>() {
            @Override
            public void onChanged(SystemInfoResponse systemInfoResponse) {
                temperatureTextView.setText("Temperature: " + systemInfoResponse.getTemperature());
                memoryTextView.setText("Memory: " + systemInfoResponse.getMemory() + "%");
                diskTextView.setText("Disk: " + systemInfoResponse.getDisk() + "%");
                powerTextView.setText("Power: " + systemInfoResponse.getPower());

                // Invia i dati alla WebView per aggiornare i grafici
                String jsCode = String.format("updateCharts(%s, %s, %s);",
                        systemInfoResponse.getTemperature(),
                        systemInfoResponse.getMemory(),
                        systemInfoResponse.getDisk());
                webView.evaluateJavascript(jsCode, null);
            }
        });

        return view;
    }
}
