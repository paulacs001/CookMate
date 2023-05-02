package com.example.cookmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String myuid;
    ActionBar actionBar;
    BottomNavigationView navigationView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";

    private static final String CHANNEL_ID = "es.uc3m.android.notifications.notify_001";
    private static final String CHANNEL_NAME = "My notification channel";

    private NotificationManager notificationManager;
    private int notificationId;

    private String NotiTitle = "Tacos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Activity");
        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar.setTitle("Home");

        // When we open the application first  time the fragment should be shown to the user
        // in this case it is home fragment
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();


        //Setting up for notifications
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> docs = task.getResult().getDocuments();
                            int size = docs.size();
                            int randomIndex = (int) (Math.random() * size);
                            DocumentSnapshot randomDoc = docs.get(randomIndex);
                            NotiTitle = randomDoc.get("title").toString();
                            Log.d(TAG, "SUGGESTION: " + NotiTitle);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //Make time so that we can fetch a recipe (3 secs)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = navigationView.getContext();
                notificationManager = (NotificationManager) context
                        .getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context, CHANNEL_ID);
                builder.setContentTitle("CookMate");
                builder.setContentText("Why not try out making " + NotiTitle + "?");
                builder.setSmallIcon(R.mipmap.ic_logo);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                    builder.setChannelId(CHANNEL_ID);
                }

                notificationId = 0;
                notificationManager.notify(notificationId, builder.build());
            }
        }, 3000);
    }
        public void toLogin(View view) {
        Intent mainIntent = new Intent(DashboardActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_recipes:
                    actionBar.setTitle(R.string.recipes_nav);
                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                case R.id.nav_search:
                    actionBar.setTitle(R.string.search_nav);
                    SearchFragment fragment1 = new SearchFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content, fragment1);
                    fragmentTransaction1.commit();
                    return true;

                case R.id.nav_shopping_list:
                    actionBar.setTitle(R.string.shop_list_nav);
                    ShoppingListFragment fragment2 = new ShoppingListFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    return true;

                case R.id.nav_profile:
                    actionBar.setTitle(R.string.profile_nav);
                    ProfileFragment listFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, listFragment, "");
                    fragmentTransaction3.commit();
                    return true;

            }
            return false;
        }
    };
}