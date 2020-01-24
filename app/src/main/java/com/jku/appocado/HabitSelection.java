package com.jku.appocado;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jku.appocado.Adapters.CustomGridAdapter;
import com.jku.appocado.Models.GridItem;
import com.jku.appocado.Models.Habit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jku.appocado.MainActivity.ANONYMOUS;


public class HabitSelection extends AppCompatActivity {

    private String mUserID;

    private ArrayList usersHabitList = new ArrayList<>();
    private ArrayList allHabitsList = new ArrayList();

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mHabitsReference;
    private DatabaseReference mUsersReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mHabitsEventListener;
    private ChildEventListener mUsersEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //UI
//    private TextView mTextView;
    private GridView mGridView;
    private CustomGridAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_selection);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mUserID = mFirebaseUser.getUid();
        }

        //Initializse Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mHabitsReference = mDatabaseReference.child("habits");
        mUsersReference = mDatabaseReference.child("users").child(mUserID);

        attachDatabaseReadListener();

        initializeUI();
    }

    private void initializeUI() {
//        mTextView = findViewById(R.id.tvTest);

        //Grid view for selected habits
        mGridView = (GridView) findViewById(R.id.grid_view);
        mAdapter = new CustomGridAdapter(this, R.layout.grid_view_item, usersHabitList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Creates new intent, passes name of habit to habit overview screen
                Intent intent = new Intent(HabitSelection.this, HabitOverview.class);
                intent.putExtra("Habit", mAdapter.getItem(position).getHabitName());
                Toast.makeText(getApplicationContext(), mAdapter.getItem(position).getHabitName(), Toast.LENGTH_SHORT).show();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users");
                DatabaseReference usersRef = ref.child(mUserID).child("habits");

                Map<String, Object> habits = new HashMap<>();
                habits.put(String.valueOf(id) , new Habit(mAdapter.getItem(position).getHabitName(), mAdapter.getItem(position).getHabitDescription()));
                usersRef.updateChildren(habits);
            }
        });

    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getKey().equals("habits")) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Habit habit;
                            habit = child.getValue(Habit.class);
                            habit.setId(dataSnapshot.getKey());
                            allHabitsList.add(habit);
                            mAdapter.add(new GridItem(habit.getName(),0, habit.getDescription()));
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

}
