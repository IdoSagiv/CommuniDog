package communi.dog.aplicatiion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import communi.dog.aplicatiion.Callbacks.OnApprovalRequestCallback;

public class UnapprovedUsersAdapter extends RecyclerView.Adapter<UnapprovedUserViewHolder> {
    private final ArrayList<User> unapprovedUsers = new ArrayList<>();
    public OnApprovalRequestCallback onApproveCallback = null;
    public OnApprovalRequestCallback onDisapproveCallback = null;

    public void setItems(ArrayList<User> unapproved) {
        unapprovedUsers.clear();
        unapprovedUsers.addAll(unapproved);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UnapprovedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approve_user_item, parent, false);
        return new UnapprovedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnapprovedUserViewHolder holder, int position) {
        User user = unapprovedUsers.get(position);

        holder.emailTextView.setText(user.getEmail());
        holder.approveBtn.setOnClickListener(v -> {
            if (onApproveCallback != null) {
                onApproveCallback.onClick(user);
            }
        });

        holder.disapproveBtn.setOnClickListener(v -> {
            if (onDisapproveCallback != null) {
                onDisapproveCallback.onClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return unapprovedUsers.size();
    }
}
