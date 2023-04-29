package com.example.cookmate;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    private FloatingActionButton btnUpdateProfile;

    private TextView display_name, display_description;

    ImageView profile_pic, gallery1, gallery2, gallery3;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "MyActivity";

    private final int PICK_IMAGE_REQUEST_PROFILE = 71;
    private final int PICK_IMAGE_REQUEST_GALLERY1 = 72;
    private final int PICK_IMAGE_REQUEST_GALLERY2 = 73;
    private final int PICK_IMAGE_REQUEST_GALLERY3 = 74;
    private String updatedField;

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
        gallery1 =  view.findViewById(R.id.gallery_img_1);
        gallery2 =  view.findViewById(R.id.gallery_img_2);
        gallery3 =  view.findViewById(R.id.gallery_img_3);

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

                        if (document.get("description") != null) {
                            display_description.setText(document.get("description").toString());
                        }

                        if (document.get("profile_pic") != null){
                            String profile_image_uri = document.get("profile_pic").toString();
                            Picasso.get().load(profile_image_uri).into(profile_pic);
                        }

                        if (document.get("gallery1") != null){
                            String gallery1_image_uri = document.get("gallery1").toString();
                            Picasso.get().load(gallery1_image_uri).into(gallery1);
                        }

                        if (document.get("gallery2") != null){
                            String gallery2_image_uri = document.get("gallery2").toString();
                            Picasso.get().load(gallery2_image_uri).into(gallery2);
                        }

                        if (document.get("gallery3") != null){
                            String gallery3_image_uri = document.get("gallery3").toString();
                            Picasso.get().load(gallery3_image_uri).into(gallery3);
                        }

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
                startActivityForResult(intent, PICK_IMAGE_REQUEST_PROFILE);

            }
        });

        gallery1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the gallery intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST_GALLERY1);
            }
        });

        gallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the gallery intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST_GALLERY2);
            }
        });

        gallery3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the gallery intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST_GALLERY3);
            }
        });


        display_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = popupEdit(0);
                Log.d(TAG, "CHANGE OF NAME!! : " + newName);
            }

        });


        display_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupEdit(1);
            }
        });

        return view;
    }

    private String popupEdit(int mode) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit, null);

        // Initialize the views inside the popup layout
        TextView popupTitle = popupView.findViewById(R.id.popup_title);
        EditText popupEditText = popupView.findViewById(R.id.popup_edit_text);
        Button updateUsernameButton = popupView.findViewById(R.id.update_desc_button);

        if (mode == 0){
            popupTitle.setText("Edit Username");
            popupEditText.setHint("Enter your new username");
        }

        else if (mode == 1){
            popupTitle.setText("Edit Description");
            popupEditText.setHint("Enter your new user description");
        }
        // Create the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        // Set the title of the popup window
        popupTitle.setText("Edit Username");

        // Set a click listener on the Update button
        updateUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new username from the EditText
                updatedField = popupEditText.getText().toString();

                if (mode == 0){
                    display_name.setText(updatedField);;
                }

                else if (mode == 1){
                    display_description.setText(updatedField);
                }



                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();

                if (mode == 0){
                    db.collection("users").document(uid)
                            .update("name", updatedField);
                }

                else if (mode == 1){
                    db.collection("users").document(uid)
                            .update("description", updatedField);
                }

                // Dismiss the popup window
                popupWindow.dismiss();

            }
        });


        // Show the popup window
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        return updatedField;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_PROFILE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageUri  = data.getData();
            uploadImageToFirestore(imageUri, 0);

            //profile_pic = filePath;
        }

        if (requestCode == PICK_IMAGE_REQUEST_GALLERY1
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageUri  = data.getData();
            uploadImageToFirestore(imageUri, 1);

            //profile_pic = filePath;
        }

        if (requestCode == PICK_IMAGE_REQUEST_GALLERY2
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageUri  = data.getData();
            uploadImageToFirestore(imageUri, 2);

            //profile_pic = filePath;
        }

        if (requestCode == PICK_IMAGE_REQUEST_GALLERY3
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageUri  = data.getData();
            uploadImageToFirestore(imageUri, 3);

            //profile_pic = filePath;

        }
    }



    private void uploadImageToFirestore(Uri imageUri, int location) {
        // Create a storage reference for the image file
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = null;

        if (location == 0) {
            storageRef = storage.getReference().child("profile_images/" + user.getUid() + ".jpg");
        }

        if (location == 1) {
            storageRef = storage.getReference().child("UserGallery/" + user.getUid() + "1.jpg");
        }

        if (location == 2) {
            storageRef = storage.getReference().child("UserGallery/" + user.getUid() + "2.jpg");
        }

        if (location == 3) {
            storageRef = storage.getReference().child("UserGallery/" + user.getUid() + "3.jpg");
        }

        // Upload the image file to Firebase Storage

        StorageReference finalStorageRef = storageRef;
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image file
                        finalStorageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Save the download URL to Firestore
                                        if (location == 0){
                                            db.collection("users").document(user.getUid())
                                                    .update("profile_pic", uri.toString());

                                            String image_uri = uri.toString();
                                            Picasso.get().load(image_uri).into(profile_pic);

                                        }

                                        else if (location == 1){
                                            db.collection("users").document(user.getUid())
                                                    .update("gallery1", uri.toString());

                                            String image_uri = uri.toString();
                                            Picasso.get().load(image_uri).into(gallery1);
                                        }

                                        else if (location == 2){
                                            db.collection("users").document(user.getUid())
                                                    .update("gallery2", uri.toString());

                                            String image_uri = uri.toString();
                                            Picasso.get().load(image_uri).into(gallery2);
                                        }

                                        else if (location == 3){
                                            db.collection("users").document(user.getUid())
                                                    .update("gallery3", uri.toString());

                                            String image_uri = uri.toString();
                                            Picasso.get().load(image_uri).into(gallery3);
                                        }
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