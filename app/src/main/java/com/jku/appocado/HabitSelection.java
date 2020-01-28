package com.jku.appocado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jku.appocado.Models.Habit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HabitSelection extends AppCompatActivity {

    private static final String USER_HABITS = "User Habit List";

    private String mUserID;

    private ArrayList allHabitsList = new ArrayList();
    private ArrayList userHabitsID = new ArrayList();
    private Map<String, Object> mSelectedHabits = new HashMap<>();

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ChildEventListener mChildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //UI
    private GridView mGridView;
    private GridAdapter mAdapter;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_selection);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) userHabitsID = bundle.getStringArrayList(USER_HABITS);

        //Initializse Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mUserID = mFirebaseUser.getUid();
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        attachDatabaseReadListener();

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initializeUI();
    }


    private void initializeUI() {
//        mTextView = findViewById(R.id.tvTest);

        //Grid view for selected habits
        mGridView = findViewById(R.id.grid_view);
        mAdapter = new GridAdapter(this, R.layout.grid_view_item, allHabitsList);
        mGridView.setAdapter(mAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Creates new intent, passes name of habit to habit overview screen
                //Intent intent = new Intent(HabitSelection.this, HabitOverview.class);
                //intent.putExtra("Habit", mAdapter.getItem(position).getName());

                if (checkIfUserHasHabit(mAdapter.getItem(position).getId())) {
                    Toast.makeText(getApplicationContext(), "Already selected", Toast.LENGTH_SHORT).show();
                } else {
                    view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rectangle_filled));
                    Toast.makeText(getApplicationContext(), mAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();

                    mSelectedHabits.put(String.valueOf(id), new Habit(mAdapter.getItem(position).getName(), mAdapter.getItem(position).getDescription(), mAdapter.getItem(position).getImage(), 0));

                }
            }
        });

        // Floating Action button to go to previous screen
        FloatingActionButton fab = findViewById(R.id.fabHabitSelection);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference usersRef = mDatabaseReference.child("users").child(mUserID).child("habits");
                usersRef.updateChildren(mSelectedHabits);
                finish();
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
                            habit.setId(child.getKey());
                            mAdapter.add(habit);

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

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    public boolean checkIfUserHasHabit(String id) {
        boolean state = false;
        for (Object s : userHabitsID) {
            if (id.equals(s.toString())) {
                state = true;
                break;
            }
        }

        return state;

    }

    public class GridAdapter extends ArrayAdapter<Habit> {

        private View mView;

        public GridAdapter(Context context, int textViewResourceId, ArrayList objects) {
            super(context, textViewResourceId, objects);

        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (view == null) {
                mView = inflater.inflate(R.layout.grid_view_item, null);
            } else {
                mView = view;
            }
            TextView textView = mView.findViewById(R.id.gridText);
            ImageView imageView = mView.findViewById(R.id.gridImage);
            textView.setText(getItem(position).getName());
            Glide.with(imageView.getContext()).load(getItem(position).getImage()).into(imageView);
            if (checkIfUserHasHabit(getItem(position).getId())) {
                mView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rectangle_filled));
            }
            return mView;
        }

        @Override
        public void add(@Nullable Habit object) {
            super.add(object);

        }
    }

}
