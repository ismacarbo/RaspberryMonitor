package com.example.raspberrymonitor.Movements;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.raspberrymonitor.Main.MainViewModel;
import com.example.raspberrymonitor.Movements.MovementsResponse;
import com.example.raspberrymonitor.R;

public class FragmentMovements extends Fragment {
    private MainViewModel viewModel;
    private TextView timestampTextView;
    private TextView detailTextView;
    private WebView webView;

    public FragmentMovements() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movements, container, false);

        timestampTextView = view.findViewById(R.id.timestampTextView);
        detailTextView = view.findViewById(R.id.detailTextView);
        webView = view.findViewById(R.id.webView);

        // Enable JavaScript and other settings for mobile view
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Enable mixed content to allow loading HTTP content from HTTPS connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Set a mobile user agent string
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.93 Mobile Safari/537.36");

        webView.setWebViewClient(new WebViewClient());

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getMovements().observe(getViewLifecycleOwner(), new Observer<MovementsResponse>() {
            @Override
            public void onChanged(MovementsResponse movementsResponse) {
                if (movementsResponse != null && movementsResponse.getMovements() != null && !movementsResponse.getMovements().isEmpty()) {
                    MovementsResponse.Movement latestMovement = movementsResponse.getMovements().get(movementsResponse.getMovements().size() - 1);
                    timestampTextView.setText("Timestamp: " + latestMovement.getTimestamp());
                    detailTextView.setText("Detail: " + latestMovement.getDetail());
                    webView.loadUrl("http://" + latestMovement.getIp() + "/");
                } else {
                    timestampTextView.setText("No movements detected.");
                    detailTextView.setText("");
                }
            }
        });

        return view;
    }
}
