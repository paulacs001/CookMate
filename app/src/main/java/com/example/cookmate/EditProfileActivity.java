package com.example.cookmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private EditText edit_name, edit_description;

    private Button btn_edit_gallery, btn_update_profile;

    private ImageView gallery_img_1;

    private Uri profile_pic;

    private static final int PICK_IMAGE = 1;
    private static final int GalleryPick = 2;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";
    private FirebaseAuth mAuth;
    private ImageButton btnProfilePic;

    private static final int REQUEST_CODE_UPP = 0x9988;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edit_name = findViewById(R.id.edit_name);
        edit_description = findViewById(R.id.edit_description);
        btn_edit_gallery = findViewById(R.id.btn_edit_gallery);
        btn_update_profile = findViewById(R.id.btn_update_profile);
        btnProfilePic = findViewById(R.id.btnProfilePic);
        gallery_img_1 = findViewById(R.id.gallery_img_1);



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();


        // Add click listener to the "Add Recipe" button
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String name = edit_name.getText().toString().trim();
                String description = edit_description.getText().toString().trim();

                // Validate user input
                if (name.isEmpty()) {
                    edit_name.setError("Please enter your name");
                    edit_name.requestFocus();
                    return;
                }

                if (description.isEmpty()) {
                    edit_description.setError("Please enter a description");
                    edit_description.requestFocus();
                    return;
                }

                // Add recipe to database or perform any other relevant action here

                // Create a new user with a first and last name
                Map<String, Object> updated_info = new HashMap<>();
                updated_info.put("name", name);
                updated_info.put("profile_pic", profile_pic);
                updated_info.put("description", description);
                updated_info.put("gallery", selectedImageUris);


                db.collection("users").document(uid)
                        .set(updated_info)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });


                // Display success message
                Toast.makeText(getApplicationContext(), "Profile successfully", Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(EditProfileActivity.this, SplashScreen.class);
                startActivity(mainIntent);

                // Clear input fields
                edit_name.setText("");
                edit_description.setText("");
            }
        });

        // Add click listener to the "Add Image" button
        btn_edit_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUris.size() < 5) {
                    // Launch photo picker
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE);
                } else {
                    Toast.makeText(getApplicationContext(), "You can only select up to 5 images", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch photo picker

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_UPP);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_UPP && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            profile_pic = imageUri;
        }

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            // Get the image URI and add it to the selectedImageUris list
            Uri imageUri = data.getData();
            selectedImageUris.add(imageUri);

            // Show the selected image(s) in the ImageView(s)
            showSelectedImages();
        }

    }
    private void showSelectedImages() {
        for (int i = 0; i < selectedImageUris.size(); i++) {
            if (i == 0) {
                // Show the first selected image in the main ImageView
                gallery_img_1.setImageURI(selectedImageUris.get(i));
            } else if (i == 1) {
                // Show the second selected image in the first additional ImageView
                ImageView gallery_img_2 = findViewById(R.id.gallery_img_2);
                gallery_img_2.setVisibility(View.VISIBLE);
                gallery_img_2.setImageURI(selectedImageUris.get(i));
            } else if (i == 2) {
                // Show the third selected image in the second additional ImageView
                ImageView gallery_img_3 = findViewById(R.id.gallery_img_3);
                gallery_img_3.setVisibility(View.VISIBLE);
                gallery_img_3.setImageURI(selectedImageUris.get(i));
            } else if (i == 3) {
                // Show the fourth selected image in the third additional ImageView
                ImageView gallery_img_4 = findViewById(R.id.gallery_img_4);
                gallery_img_4.setVisibility(View.VISIBLE);
                gallery_img_4.setImageURI(selectedImageUris.get(i));
            } else if (i == 4) {
                // Show the fifth selected image in the fourth additional ImageView
                ImageView gallery_img_5 = findViewById(R.id.gallery_img_5);
                gallery_img_5.setVisibility(View.VISIBLE);
                gallery_img_5.setImageURI(selectedImageUris.get(i));
            }
        }
    }

}
