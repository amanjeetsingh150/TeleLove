package com.developers.telelove;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Amanjeet Singh on 31/12/17.
 */

public class QuoteJobService extends JobService {


    private static final String TAG = QuoteJobService.class.getSimpleName();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<String> authorList, quoteList;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG, "started JoB");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("quotes");
        getQuote(databaseReference);
        authorList = new ArrayList<>();
        quoteList = new ArrayList<>();
        return false;
    }

    private void getQuote(DatabaseReference databaseReference) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d(TAG, data.getValue() + " ");
                    for (DataSnapshot snapshot : data.getChildren()) {
                        if (snapshot.getKey().equals("author")) {
                            authorList.add(snapshot.getValue() + "");
                            Log.d(TAG, " KEY-> Author-> " + snapshot.getValue());
                        }
                        if (snapshot.getKey().equals("quote")) {
                            quoteList.add(snapshot.getValue() + "");
                            Log.d(TAG, "KEY->Quote-> " + snapshot.getValue());
                        }
                    }
                }
                Log.d(TAG, "Size " + quoteList.size() + " " + authorList.size());
                showQuote(authorList, quoteList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onQuotesCancelled: ", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(eventListener);

    }

    private void showQuote(List<String> authorList, List<String> quoteList) {
        int index = new Random().nextInt(quoteList.size() - 1);

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
