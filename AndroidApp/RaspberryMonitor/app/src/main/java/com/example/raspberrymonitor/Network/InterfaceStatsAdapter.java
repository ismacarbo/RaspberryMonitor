package com.example.raspberrymonitor.Network;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.raspberrymonitor.R;
import java.util.List;
import java.util.Map;

public class InterfaceStatsAdapter extends RecyclerView.Adapter<InterfaceStatsAdapter.InterfaceStatsViewHolder> {

    private List<Map.Entry<String, NetworkInfoResponse.InterfaceStats>> interfacesList;

    public InterfaceStatsAdapter(List<Map.Entry<String, NetworkInfoResponse.InterfaceStats>> interfacesList) {
        this.interfacesList = interfacesList;
    }

    @NonNull
    @Override
    public InterfaceStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interface_stats, parent, false);
        return new InterfaceStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterfaceStatsViewHolder holder, int position) {
        Map.Entry<String, NetworkInfoResponse.InterfaceStats> entry = interfacesList.get(position);
        holder.interfaceNameTextView.setText(entry.getKey());
        holder.bytesSentTextView.setText("Bytes Sent: " + entry.getValue().getSent());
        holder.bytesRecvTextView.setText("Bytes Received: " + entry.getValue().getRecv());
    }

    @Override
    public int getItemCount() {
        return interfacesList.size();
    }

    static class InterfaceStatsViewHolder extends RecyclerView.ViewHolder {
        TextView interfaceNameTextView;
        TextView bytesSentTextView;
        TextView bytesRecvTextView;

        public InterfaceStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            interfaceNameTextView = itemView.findViewById(R.id.interfaceNameTextView);
            bytesSentTextView = itemView.findViewById(R.id.bytesSentTextView);
            bytesRecvTextView = itemView.findViewById(R.id.bytesRecvTextView);
        }
    }
}
