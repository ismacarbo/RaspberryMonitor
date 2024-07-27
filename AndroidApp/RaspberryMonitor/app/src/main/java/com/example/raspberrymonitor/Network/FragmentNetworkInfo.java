package com.example.raspberrymonitor.Network;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.raspberrymonitor.Main.MainViewModel;
import com.example.raspberrymonitor.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentNetworkInfo extends Fragment {
    private MainViewModel viewModel;
    private TextView totalBytesSentTextView;
    private TextView totalBytesRecvTextView;
    private WebView webView;
    private RecyclerView interfacesRecyclerView;
    private InterfaceStatsAdapter adapter;

    public FragmentNetworkInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_info, container, false);

        totalBytesSentTextView = view.findViewById(R.id.totalBytesSentTextView);
        totalBytesRecvTextView = view.findViewById(R.id.totalBytesRecvTextView);
        webView = view.findViewById(R.id.webView);
        interfacesRecyclerView = view.findViewById(R.id.interfacesRecyclerView);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Set up the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/network_chart.html");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("MyApplication", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
        });

        // Set up the RecyclerView
        interfacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InterfaceStatsAdapter(new ArrayList<>());
        interfacesRecyclerView.setAdapter(adapter);

        viewModel.getNetworkInfo().observe(getViewLifecycleOwner(), new Observer<NetworkInfoResponse>() {
            @Override
            public void onChanged(NetworkInfoResponse networkInfoResponse) {
                if (networkInfoResponse != null) {
                    totalBytesSentTextView.setText("Total Bytes Sent: " + networkInfoResponse.getTotal_bytes_sent());
                    totalBytesRecvTextView.setText("Total Bytes Received: " + networkInfoResponse.getTotal_bytes_recv());

                    // Log per il debug
                    Log.d("NetworkInfo", "Total Bytes Sent: " + networkInfoResponse.getTotal_bytes_sent());
                    Log.d("NetworkInfo", "Total Bytes Received: " + networkInfoResponse.getTotal_bytes_recv());

                    // Aggiorna i dati dell'adattatore
                    List<Map.Entry<String, NetworkInfoResponse.InterfaceStats>> interfacesList = new ArrayList<>(networkInfoResponse.getInterfaces().entrySet());
                    adapter = new InterfaceStatsAdapter(interfacesList);
                    interfacesRecyclerView.setAdapter(adapter);

                    // Invia i dati alla WebView per aggiornare i grafici del network
                    String jsCode = String.format("updateNetworkCharts(%s, %s);",
                            networkInfoResponse.getTotal_bytes_sent(),
                            networkInfoResponse.getTotal_bytes_recv());
                    webView.evaluateJavascript(jsCode, null);
                } else {
                    Log.e("NetworkInfo", "Response is null");
                }
            }
        });

        return view;
    }
}
