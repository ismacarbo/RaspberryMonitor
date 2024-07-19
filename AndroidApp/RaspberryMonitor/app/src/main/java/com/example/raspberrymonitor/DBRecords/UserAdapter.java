package com.example.raspberrymonitor.DBRecords;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.raspberrymonitor.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    // Constructor
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.idTextView.setText(String.valueOf(user.getId()));
        holder.nomeTextView.setText(user.getNome());
        holder.cognomeTextView.setText(user.getCognome());
        holder.cellulareTextView.setText(user.getCellulare());
        holder.provenienzaTextView.setText(user.getProvenienza());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView nomeTextView;
        TextView cognomeTextView;
        TextView cellulareTextView;
        TextView provenienzaTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.text_view_id);
            nomeTextView = itemView.findViewById(R.id.text_view_nome);
            cognomeTextView = itemView.findViewById(R.id.text_view_cognome);
            cellulareTextView = itemView.findViewById(R.id.text_view_cellulare);
            provenienzaTextView = itemView.findViewById(R.id.text_view_provenienza);
        }
    }
}
