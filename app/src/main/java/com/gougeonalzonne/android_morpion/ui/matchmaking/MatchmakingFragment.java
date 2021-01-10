package com.gougeonalzonne.android_morpion.ui.matchmaking;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gougeonalzonne.android_morpion.R;
import com.gougeonalzonne.android_morpion.ui.UserViewModel;
import com.gougeonalzonne.android_morpion.ui.leaderboard.LeaderboardAdapter;
import com.gougeonalzonne.android_morpion.ui.leaderboard.LeaderboardItem;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchmakingFragment extends Fragment {

    private MatchmakingViewModel matchmakingViewModel;
    private UserViewModel userViewModel;
    ListenerRegistration mm = null;
    ListenerRegistration gj = null;
    Handler h = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        matchmakingViewModel =
                ViewModelProviders.of(this).get(MatchmakingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_matchmaking, container, false);



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Date now = new Date();
        final boolean[] gameCreated = {false};
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(final String user) {
                final TextView text_user = (TextView) getView().findViewById(R.id.matchmaking_user);
                final TextView text_mm = (TextView) getView().findViewById(R.id.matchmaking_text);
                if (text_user != null) {
                    text_user.setText(user);
                }
                h.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("user", user);
                        entry.put("joinedAt", new Timestamp(now));
                        entry.put("refreshedAt", new Timestamp(new Date()));
                        db.collection("matchmaking").document(user)
                            .set(entry)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(text_mm != null) {
                                        text_mm.setText("En attente d'un adversaire...");
                                    }
                                }
                            });
                        if(text_mm != null && text_user != null) {
                            h.postDelayed(this, 2000);
                        }

                    }
                }, 10);

                //Matchmaking queue subscribe
                Query matchmaking = db.collection("matchmaking").whereGreaterThan("refreshedAt", new Timestamp(now))
                        .orderBy("refreshedAt", Query.Direction.DESCENDING).limit(20);
                mm = matchmaking.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : snapshot) {
                            if(doc.get("user").toString().equals(user)) {
                                continue;
                            }
                            //Si j'ai join la queue avant lui, je créer le gamestate
                            Timestamp timestamp = (Timestamp) doc.get("joinedAt");
                            if(now.compareTo(timestamp.toDate()) <= 0 && gameCreated[0] == false && !doc.get("user").toString().equals(user)) {
                                Log.d("CREATE GAME", "SELF = " + user);
                                Log.d("CREATE GAME", "OTHER = " + doc.get("user").toString());
                                Log.d("IS EQUAL ?", new Boolean(user == doc.get("user").toString()).toString());
                                gameCreated[0] = true;
                                // création de la grille
                                List<String> game = new ArrayList<String>() ;
                                for (int i = 0; i < 9; i++) {
                                    game.add("");
                                }
                                Map<String, Object> entry = new HashMap<>();
                                entry.put("createdAt", new Timestamp(new Date()));
                                entry.put("p1", user);
                                entry.put("p2", doc.get("user").toString());
                                entry.put("grid", game);
                                entry.put("turn", doc.get("user").toString());
                                db.collection("games").document(user)
                                        .set(entry)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Go to GAME VIEW
                                                userViewModel.setGame(user);
                                                db.collection("matchmaking").document(user).delete();
                                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                                navController.navigate(R.id.nav_game);
                                            }
                                        });
                            }
                        }
                    }
                });

                Query gameJoined = db.collection("games").whereGreaterThan("createdAt", new Timestamp(now))
                        .whereEqualTo("p2", user).limit(1);
                gj = gameJoined.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if(snapshot != null) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                Log.d("FOUND GAME", "FOUND GAME !!!!!!!!!!!!!!!!!!!");
                                gameCreated[0] = true;
                                userViewModel.setGame(doc.get("p1").toString());
                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                navController.navigate(R.id.nav_game);
                            }
                        }
                    }
                });
            }
        });



    }

    @Override
    public void onStop() {
        super.onStop();
        if(gj != null) { gj.remove(); }
        if(mm != null) { mm.remove(); }
    }
}