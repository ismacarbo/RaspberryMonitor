package com.example.raspberrymonitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class FragmentMovements extends Fragment {
    private MainViewModel viewModel;
    private TextView movementsTextView;

    public FragmentMovements() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movements, container, false);
        movementsTextView = view.findViewById(R.id.movementsTextView);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getMovements().observe(getViewLifecycleOwner(), new Observer<List<MovementsResponse.Movement>>() {
            @Override
            public void onChanged(List<MovementsResponse.Movement> movements) {
                if (movements != null && !movements.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (MovementsResponse.Movement movement : movements) {
                        sb.append("Timestamp: ").append(movement.getTimestamp()).append("\n");
                        sb.append("Detail: ").append(movement.getDetail()).append("\n\n");
                    }
                    movementsTextView.setText(sb.toString());
                }
            }
        });

        String token = viewModel.getToken();
        if (token != null) {
            viewModel.fetchMovements(token);
        }

        return view;
    }
}
