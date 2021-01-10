package com.gougeonalzonne.android_morpion.ui.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gougeonalzonne.android_morpion.R;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment {

    private LeaderboardViewModel leaderboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        leaderboardViewModel =
                ViewModelProviders.of(this).get(LeaderboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);


        final ListView lv = (ListView) root.findViewById(R.id.leaderboard_list);

        final ArrayList<LeaderboardItem> results = new ArrayList<LeaderboardItem>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query users = db.collection("users").orderBy("win", Query.Direction.DESCENDING).limit(20);
        Log.d("OOO", "-------------------------------------------");
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("COMPLETE", "COMPLETE");
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        LeaderboardItem item = new LeaderboardItem();
                        Log.d("---- USERS :", document.get("pseudo").toString());
                        item.setTitle(document.get("pseudo").toString());
                        item.setSubTitle(document.get("win").toString() + "W " + document.get("draw").toString() + "D " + document.get("lose").toString() + "L");
                        results.add(item);
                    }
                    lv.setAdapter(new LeaderboardAdapter(getContext(), results));
                }
            }
        });



        return root;
    }
}