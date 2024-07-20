package com.example.raspberrymonitor.Movements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.raspberrymonitor.Main.MainViewModel;
import com.example.raspberrymonitor.R;

public class FragmentMovements extends Fragment {
    private MainViewModel viewModel;
    private TextView timestampTextView;
    private TextView detailTextView;

    public FragmentMovements() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movements, container, false);

        timestampTextView = view.findViewById(R.id.timestampTextView);
        detailTextView = view.findViewById(R.id.detailTextView);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getMovements().observe(getViewLifecycleOwner(), new Observer<MovementsResponse>() {
            @Override
            public void onChanged(MovementsResponse movementsResponse) {
                if (movementsResponse != null && movementsResponse.getMovements() != null && !movementsResponse.getMovements().isEmpty()) {
                    // Assuming we are interested in the latest movement
                    MovementsResponse.Movement latestMovement = movementsResponse.getMovements().get(movementsResponse.getMovements().size() - 1);
                    timestampTextView.setText("Timestamp: " + latestMovement.getTimestamp());
                    detailTextView.setText("Detail: " + latestMovement.getDetail());
                } else {
                    timestampTextView.setText("No movements detected.");
                    detailTextView.setText("");
                }
            }
        });

        return view;
    }
}
