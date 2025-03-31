package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.activities.CourtDetailsFragment;
import com.example.courtconnect3.R;
import java.util.List;

public class FavoriteCourtsAdapter extends RecyclerView.Adapter<FavoriteCourtsAdapter.ViewHolder> {

    private List<String> courtNames;
    private FragmentManager fragmentManager;

    public FavoriteCourtsAdapter(List<String> courtNames, FragmentManager fragmentManager) {
        this.courtNames = courtNames;
        this.fragmentManager = fragmentManager;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.favorite_court_name);
        }
    }

    @NonNull
    @Override
    public FavoriteCourtsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_court_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCourtsAdapter.ViewHolder holder, int position) {
        String courtName = courtNames.get(position);
        holder.courtName.setText(courtName);

        holder.itemView.setOnClickListener(v -> {
            // נפתח CourtDetailsFragment עם שם המגרש בלבד (הכתובת והתאורה ריקים כרגע)
            CourtDetailsFragment fragment = CourtDetailsFragment.newInstance(courtName, "", "");
            fragment.show(fragmentManager, "CourtDetailsFragment");
        });
    }

    @Override
    public int getItemCount() {
        return courtNames.size();
    }
}
