package com.gougeonalzonne.android_morpion.ui.game;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.gougeonalzonne.android_morpion.R;
import com.gougeonalzonne.android_morpion.ui.UserViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameFragment extends Fragment {

    private GameViewModel mViewModel;
    private UserViewModel userViewModel;
    private DocumentSnapshot gameState = null;
    private String gamename = null;
    private String username = null;
    private String winner = "";

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final ArrayList<ImageView> cases = new ArrayList<ImageView>();
        cases.add((ImageView)getView().findViewById(R.id.top_left));
        cases.add((ImageView)getView().findViewById(R.id.top));
        cases.add((ImageView)getView().findViewById(R.id.top_right));
        cases.add((ImageView)getView().findViewById(R.id.left));
        cases.add((ImageView)getView().findViewById(R.id.middle));
        cases.add((ImageView)getView().findViewById(R.id.right));
        cases.add((ImageView)getView().findViewById(R.id.bottom_left));
        cases.add((ImageView)getView().findViewById(R.id.bottom));
        cases.add((ImageView)getView().findViewById(R.id.bottom_right));

        //img.setImageDrawable(getResources().getDrawable(R.drawable.ic_p2));
        for (int i = 0; i < cases.size(); i++) {
            final int finalI = i;
            cases.get(i).setAlpha((float) 0);
            cases.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("CLICKED ON TOP LEFT", "onClick: " + finalI);
                    if(gameState != null && winner.equals("")) {
                        if(gameState.get("turn").toString().equals(username))  {
                            Log.d("test", "test");
                            List<String> grid = (List<String>) gameState.get("grid");
                            if(grid.get(finalI).equals("")) {
                                //UPDATE
                                grid.set(finalI, username);
                                Map<String, Object> entry = new HashMap<>();
                                entry.put("grid", grid);
                                if(gameState.get("p1").toString().equals(username)) {
                                    entry.put("turn", gameState.get("p2").toString());
                                }
                                else {
                                    entry.put("turn", gameState.get("p1").toString());
                                }
                                db.collection("games").document(gamename).update(entry);
                            }
                            Log.d("GRID I = ", "test + " + grid.get(finalI));

                        }

                        Log.d("Game state P1 = ", gameState.get("p1").toString());
                        Log.d("Game state P2 = ", gameState.get("p2").toString());
                        Log.d("Game state TURN = ", gameState.get("turn").toString());
                        Log.d("Game state USER = ", username);
                    }

                }
            });
        }

        Button btnRep = getView().findViewById(R.id.replay);
        btnRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gameState != null && !winner.equals("")) {
                    List<String> grid = (List<String>) gameState.get("grid");
                    for (int i = 0; i < grid.size(); i++) {
                        grid.set(i, "");
                    }
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("grid", grid);
                    db.collection("games").document(gamename).update(entry);
                }


            }
        });


        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(final String user) {
                username = user;
                userViewModel.getGame().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(final String game) {
                        gamename = game;
                        //DocumentReference gamestate = db.collection("games").document(game);
                        final DocumentReference docRef = db.collection("games").document(game);
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    gameState = snapshot;
                                    TextView p1 = getView().findViewById(R.id.p1_text);
                                    if(p1 != null) { p1.setText(snapshot.get("p1").toString()); }

                                    TextView p2 = getView().findViewById(R.id.p2_text);
                                    if(p2 != null) { p2.setText(snapshot.get("p2").toString()); }

                                    TextView turn = getView().findViewById(R.id.turn);
                                    if(turn != null) { turn.setText(snapshot.get("turn").toString()); }

                                    List<String> grid = (List<String>) gameState.get("grid");
                                    for (int i = 0; i < cases.size(); i++) {
                                        if(grid.get(i).equals(snapshot.get("p1").toString())) {
                                            cases.get(i).setAlpha((float) 1);
                                            cases.get(i).setImageDrawable(getResources().getDrawable(R.drawable.ic_p1));
                                        } else  if(grid.get(i).equals(snapshot.get("p2").toString())) {
                                            cases.get(i).setAlpha((float) 1);
                                            cases.get(i).setImageDrawable(getResources().getDrawable(R.drawable.ic_p2));
                                        } else {
                                            cases.get(i).setAlpha((float) 0);
                                        }
                                    }

                                    //check if winner
                                    //check row
                                    winner = "";
                                    for (int i = 0; i < 3 ; i++) {
                                        if(grid.get(i * 3).equals(grid.get(i * 3 + 1)) && grid.get(i * 3 + 1).equals(grid.get(i * 3 + 2))) {
                                            if(!grid.get(i * 3).equals("")) { winner = grid.get(i * 3); }
                                        }
                                    }

                                    //check col
                                    for (int i = 0; i < 3 ; i++) {
                                        if(grid.get(i).equals(grid.get(i + 3)) && grid.get((i + 3)).equals(grid.get(i + 6))) {
                                            if(!grid.get(i).equals("")) { winner = grid.get(i); }
                                        }
                                    }

                                    //check diag
                                    if(grid.get(0).equals(grid.get(4)) && grid.get(4).equals(grid.get(8))) {
                                        if(!grid.get(0).equals("")) { winner = grid.get(0); }
                                    }

                                    //other diag
                                    if(grid.get(2).equals(grid.get(4)) && grid.get(4).equals(grid.get(6))) {
                                        if(!grid.get(2).equals("")) { winner = grid.get(2); }
                                    }

                                    //check if draw
                                    boolean isDraw = true;
                                    for (int i = 0; i < grid.size(); i++) {
                                        if(grid.get(i).equals("")) { isDraw = false; }
                                    }
                                    if(isDraw) { winner = "DRAW"; }

                                    Button replay = getView().findViewById(R.id.replay);
                                    if(!winner.equals("")) {
                                        turn.setText("WINNER IS " + winner);
                                        replay.setAlpha((float) 1);
                                    }
                                    else {
                                        replay.setAlpha((float) 0);
                                    }



                                    Log.d("test", "Current data: " + snapshot.getData());
                                } else {
                                    Log.d("test", "Current data: null");
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}