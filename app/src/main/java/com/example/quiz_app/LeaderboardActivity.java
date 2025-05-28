package com.example.quiz_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter();
        recyclerView.setAdapter(adapter);

        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize Firebase Database reference
        scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        loadScores();
    }

    private void loadScores() {
        scoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserScore> scores = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    Integer score = userSnapshot.child("score").getValue(Integer.class);
                    if (username != null && score != null) {
                        scores.add(new UserScore(username, score));
                    }
                }
                // Sort scores in descending order
                Collections.sort(scores, (a, b) -> b.score - a.score);
                adapter.setScores(scores);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private static class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
        private List<UserScore> scores = new ArrayList<>();

        void setScores(List<UserScore> newScores) {
            this.scores = newScores;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_leaderboard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            UserScore score = scores.get(position);
            holder.rankTextView.setText(String.valueOf(position + 1));
            holder.usernameTextView.setText(score.username);
            holder.scoreTextView.setText(String.valueOf(score.score));
        }

        @Override
        public int getItemCount() {
            return scores.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView rankTextView;
            TextView usernameTextView;
            TextView scoreTextView;

            ViewHolder(View view) {
                super(view);
                rankTextView = view.findViewById(R.id.rankTextView);
                usernameTextView = view.findViewById(R.id.usernameTextView);
                scoreTextView = view.findViewById(R.id.scoreTextView);
            }
        }
    }

    private static class UserScore {
        String username;
        int score;

        UserScore(String username, int score) {
            this.username = username;
            this.score = score;
        }
    }
}