package com.example.raspberrymonitor.DBRecords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.raspberrymonitor.Main.MainViewModel;
import com.example.raspberrymonitor.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentDbRecords extends Fragment {
    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private Spinner databaseSpinner;
    private Button fetchDataButton;

    public FragmentDbRecords() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_db_records, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        databaseSpinner = view.findViewById(R.id.databaseSpinner);
        fetchDataButton = view.findViewById(R.id.fetchDataButton);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Configure the database spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.database_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        databaseSpinner.setAdapter(adapter);

        // Initialize the RecyclerView and its adapter
        userList = new ArrayList<>();
        this.adapter = new UserAdapter(userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.adapter);

        viewModel.getDbRecords().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    userList.clear();
                    userList.addAll(users);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDb = databaseSpinner.getSelectedItem().toString();
                viewModel.fetchDbRecords(selectedDb);
            }
        });

        return view;
    }
}
