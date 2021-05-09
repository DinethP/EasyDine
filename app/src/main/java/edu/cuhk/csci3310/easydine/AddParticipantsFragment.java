package edu.cuhk.csci3310.easydine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class AddParticipantsFragment extends Fragment {
    private String TAG = "AddParticipantFragment";
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    private LinkedList<User> userNames = new LinkedList<User>();

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_participants, container, false);
        Log.d(TAG, userNames.toString());
        // get all registered users from firestore
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // get currentUserId
                String currUserUid = user.getUid();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        // add all users except current user
                        if(document.getId().equals(currUserUid)) {
                            continue;
                        }
                        else {
                            userNames.add(document.toObject(User.class));
                        }
                    }
                    // firestore takes time to load, so names get after view is created
                    // so notify adpater to show  updated names
                    userListAdapter.notifyDataSetChanged();
                    Log.d(TAG, userNames.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        recyclerView = view.findViewById(R.id.recyclerview);
        userListAdapter = new UserListAdapter(getContext(), userNames);
        recyclerView.setAdapter(userListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    // send selected particpants linkedlist from userListAdapter to PlacesActivity
    public LinkedList<User> getSelectedParticpants(){
        return userListAdapter.getSelectedParticpants();
    }
}