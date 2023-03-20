package com.example.cookmate;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private FloatingActionButton btnUpdateProfile;

    private TextView display_name, display_description;

    ImageView profile_pic;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "MyActivity";

    public ProfileFragment() {
        // Required empty public constructor
        
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
                FloatingActionButton fab = view.findViewById(R.id.btnEditProfile);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        display_name = view.findViewById(R.id.display_name);
        display_description = view.findViewById(R.id.display_description);
        profile_pic =  view.findViewById(R.id.display_profile_pic);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        display_name.setText(document.get("name").toString());
                        display_description.setText(document.get("description").toString());

                        String image_uri = document.get("profile_pic").toString();

                        profile_pic.setVisibility(View.VISIBLE);

                        //String imageUri = "https://i.imgur.com/tGbaZCY.jpg";
                        Picasso.get().load(image_uri).into(profile_pic);


                        //profile_pic.setImageURI(Uri.parse(image_uri));

                        //profile_pic.setImageURI(null);
                        //Picasso.get().load("http://www.google.com/image.png").into(profile_pic);
                        //profile_pic.setImageURI(Uri.parse((String) document.get("profile_pic")));

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return view;
    }

}