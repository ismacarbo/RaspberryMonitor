package com.example.raspberrymonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentLogout extends Fragment {
    private MainViewModel viewModel;
    private Button logoutButton;

    public FragmentLogout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        logoutButton = view.findViewById(R.id.logoutButton);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    private void logout() {
        String token = viewModel.getToken();

        if (token != null) {
            ApiService apiService = RetrofitInstance.getApiService();
            Call<LogoutResponse> call = apiService.logout("Bearer " + token);

            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Clear the token from SharedPreferences
                        requireActivity().getSharedPreferences("prefs", requireActivity().MODE_PRIVATE)
                                .edit()
                                .remove("token")
                                .apply();
                        // Go back to LoginActivity
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Logout failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "Logout error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No token found", Toast.LENGTH_SHORT).show();
        }
    }
}
