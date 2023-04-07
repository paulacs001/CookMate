package com.example.cookmate;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private FloatingActionButton btnUpdateProfile;

    private TextView display_name, display_description;

    ImageView profile_pic;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "MyActivity";

    private final int PICK_IMAGE_REQUEST = 71;

    public ProfileFragment() {
        // Required empty public constructor
        
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
                FloatingActionButton fab = view.findViewById(R.id.btnLogOut);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(), LoginActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
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
                        Picasso.get().load(image_uri).into(profile_pic);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the gallery intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        display_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the popup layout
                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit_username, null);

                // Initialize the views inside the popup layout
                TextView popupTitle = popupView.findViewById(R.id.popup_title);
                EditText newUsernameEditText = popupView.findViewById(R.id.new_username_edit_text);
                Button updateUsernameButton = popupView.findViewById(R.id.update_username_button);

                // Create the popup window
                PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                // Set the title of the popup window
                popupTitle.setText("Edit Username");

                // Set a click listener on the Update button
                updateUsernameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the new username from the EditText
                        String newUsername = newUsernameEditText.getText().toString();
                        Log.d("DEBUG", "New username: " + newUsername);

                        db.collection("users").document(uid)
                                .update("name", newUsername);

                        // Dismiss the popup window
                        popupWindow.dismiss();
                    }
                });

                // Show the popup window
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        display_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the popup layout
                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit_desc, null);

                // Initialize the views inside the popup layout
                TextView popupTitle = popupView.findViewById(R.id.popup_title);
                EditText newDescEditText = popupView.findViewById(R.id.new_desc_edit_text);
                Button updateUsernameButton = popupView.findViewById(R.id.update_desc_button);

                // Create the popup window
                PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                // Set the title of the popup window
                popupTitle.setText("Edit Username");

                // Set a click listener on the Update button
                updateUsernameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the new username from the EditText
                        String newDesc = newDescEditText.getText().toString();
                        Log.d("DEBUG", "New desc: " + newDesc);

                        db.collection("users").document(uid)
                                .update("description", newDesc);

                        // Dismiss the popup window
                        popupWindow.dismiss();
                    }
                });

                // Show the popup window
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageUri  = data.getData();
            uploadImageToFirestore(imageUri);

            //profile_pic = filePath;
        }
    }

    private void uploadImageToFirestore(Uri imageUri) {
        // Create a storage reference for the image file
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + user.getUid() + ".jpg");

        // Upload the image file to Firebase Storage

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image file
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Save the download URL to Firestore
                                        db.collection("users").document(user.getUid())
                                                .update("profile_pic", uri.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display an error message
                        Toast.makeText(getActivity(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                    }
                });


    }


}