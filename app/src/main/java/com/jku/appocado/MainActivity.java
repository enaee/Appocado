package com.jku.appocado;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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
import com.jku.appocado.Models.Habit;
import com.jku.appocado.Models.Score;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final String HABIT_NAME = "habit name";
    public static final String USER_ID = "userID";
    public static final String HABIT_DESCRIPTION = "habit description";
    public static final String HABIT_ID = "habitID";

    private static final String TAG = "MainActivity";
    private static final String USER_HABITS = "User Habit List";
    private static final int RC_SIGN_IN = 123;
    public static ArrayList usersHabitList = new ArrayList<>();
    private String mUserID;
    private ArrayList userHabitIDList = new ArrayList<>();
    private CustomGridAdapter mAdapter;
    private ProgressBar mProgressBar;
    private Score mScore;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mHabitsReference;
    private DatabaseReference mUsersReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mHabitsEventListener;
    private ChildEventListener mUsersEventListener;

    //UI
//    private TextView mTextView;
    private GridView mGridView;
    private TextView mTotalActions, mDaysTotal, mStrikeDays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserID = ANONYMOUS;
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        //Initializse Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mHabitsReference = mDatabaseReference.child("habits");
        mUsersReference = mDatabaseReference.child("users").child(mUserID);


        initializeFirebaseAuth();
        initializeUI();

    }

    protected void onStart(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserID = ANONYMOUS;

        //Initializse Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mHabitsReference = mDatabaseReference.child("habits");
        mUsersReference = mDatabaseReference.child("users").child(mUserID);


        initializeFirebaseAuth();
        initializeUI();

    }

    private void initializeUI() {
        mTotalActions = findViewById(R.id.totalActionsMade);
        mDaysTotal = findViewById(R.id.totalDays);
        mStrikeDays = findViewById(R.id.strikeDays);

        //Grid view for selected habits
        mGridView = findViewById(R.id.grid_view);
        mAdapter = new CustomGridAdapter(this, R.layout.grid_view_item, usersHabitList);
        mGridView.setAdapter(mAdapter);

        setGridClickListener();


        // Floating Action button to go to select new habits
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates new intent, passes name of habit to habit overview screen
                Intent intent = new Intent(MainActivity.this, HabitSelection.class);
                intent.putStringArrayListExtra(USER_HABITS, userHabitIDList);
                startActivity(intent);
            }
        });
    }

    private void setGridClickListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //add value to habit

                setScoreData();


                Habit habit = mAdapter.getItem(position);
                int count = habit.getCount() + 1;
                mScore.setTotalActions(mScore.getTotalActions() + 1);

                //int count = Integer.parseInt(habit.getCount())+1;
                mDatabaseReference.child("users/" + mUserID + "/habits/" + habit.getId() + "/count").setValue(count);

                // Creates new intent, passes habit information to new activity
                Intent intent = new Intent(MainActivity.this, HabitOverview.class);
                intent.putExtra(HABIT_NAME, habit.getName());
                intent.putExtra(USER_ID, mUserID);
                intent.putExtra(HABIT_ID, habit.getId());
                Toast.makeText(getApplicationContext(), habit.getName(), Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, 12);
            }
        });
    }

    private void setScoreData() {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyMMdd");
        String date = simpledateformat.format(Calendar.getInstance().getTime());
        mDatabaseReference.child("users/" + mUserID + "/score/lastInput").setValue(date);
        mDatabaseReference.child("users/" + mUserID + "/score/totalActions").setValue(mScore.getTotalActions() + 1);
        if (!date.equals(mScore.getLastInput())) {
            mDatabaseReference.child("users/" + mUserID + "/score/totalDays").setValue(mScore.getTotalDays() + 1);
            int lastDate = Integer.parseInt(mScore.getLastInput());
            int newDate = Integer.parseInt(date);
            if (lastDate == (newDate - 1)) {
                mDatabaseReference.child("users/" + mUserID + "/score/strike").setValue(mScore.getStrike() + 1);
            }
        }

    }

    private void initializeFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    onSignedInInitialize();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    mUserID = mFirebaseUser.getUid();
                }
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign In Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void onSignedInInitialize() {
        mUserID = mFirebaseUser.getUid();
        mFirebaseDatabase.getReference().child("users").child(mUserID).child("name").setValue(mFirebaseUser.getDisplayName());
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (mUserID.equals(child.getKey())) {
                            for (DataSnapshot user_information : child.getChildren()) {
                                if ("habits".equals(user_information.getKey())) {
                                    for (DataSnapshot habits : user_information.getChildren()) {
                                        Habit habit;
                                        habit = habits.getValue(Habit.class);
                                        habit.setId(habits.getKey());
                                        userHabitIDList.add(habits.getKey());
                                        mAdapter.add(habit);
                                    }
                                }
                                if ("score".equals(user_information.getKey())) {
                                    Score score;
                                    score = user_information.getValue(Score.class);
                                    mScore = score;
                                    updateScore();
                                }
                            }
                        }
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mAdapter.clear();
                    userHabitIDList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (mUserID.equals(child.getKey())) {
                            for (DataSnapshot user_information : child.getChildren()) {
                                if ("habits".equals(user_information.getKey())) {
                                    for (DataSnapshot habits : user_information.getChildren()) {
                                        Habit habit;
                                        habit = habits.getValue(Habit.class);
                                        habit.setId(habits.getKey());
                                        userHabitIDList.add(habits.getKey());
                                        mAdapter.add(habit);
                                    }
                                }
                            }
                        }
                    }
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

    private void updateScore() {
        mTotalActions.setText(Integer.toString(mScore.getTotalActions()));
        mDaysTotal.setText(Integer.toString(mScore.getTotalDays()));
        mStrikeDays.setText(Integer.toString(mScore.getStrike()));
    }

    private void onSignedOutCleanup() {
        mUserID = ANONYMOUS;

        //mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mHabitsEventListener != null) {
            mHabitsReference.removeEventListener(mHabitsEventListener);
            mHabitsEventListener = null;
        }
        if (mUsersEventListener != null) {
            mUsersReference.removeEventListener(mHabitsEventListener);
            mUsersEventListener = null;
        }
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.VISIBLE);
        userHabitIDList.clear();
        mAdapter.clear();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        attachDatabaseReadListener();
    }

    //Why is this here?
    /*
    @Override
    protected void onRestart() {
        super.onRestart();

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot habit_values : dataSnapshot.getChildren()) {
                            if (dataSnapshot.getKey().equals(mUserID)) {
                                for (DataSnapshot habits : habit_values.getChildren()) {
                                    Habit habit = new Habit(habits.getValue().toString(), habits.getKey());
                                    habit.setId(dataSnapshot.getKey());
                                    allHabitsList.add(habit);
                                    mAdapter.add(habit);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    } */
}
