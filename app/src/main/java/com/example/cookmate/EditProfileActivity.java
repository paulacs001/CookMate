package com.example.cookmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {
    private EditText edit_name, edit_description;

    private Button btn_edit_gallery, btn_update_profile;

    private ImageView gallery_img_1;

    private String profile_pic;

    private static final int PICK_IMAGE = 1;
    private static final int GalleryPick = 2;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";
    private FirebaseAuth mAuth;
    private ImageButton btnProfilePic;

    private static final int REQUEST_CODE_UPP = 0x9988;

    Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edit_name = findViewById(R.id.edit_name);
        edit_description = findViewById(R.id.edit_description);
        btn_update_profile = findViewById(R.id.btn_update_profile);
        btnProfilePic = findViewById(R.id.btnProfilePic);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();


        // Add click listener to the "Add Recipe" button
        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();


                String name = edit_name.getText().toString().trim();
                String description = edit_description.getText().toString().trim();

                // Validate user input
                if (name.isEmpty()) {
                    edit_name.setError(getString(R.string.edit_profile_warning_name));
                    edit_name.requestFocus();
                }

                if (description.isEmpty()) {
                    edit_description.setError(getString(R.string.edit_profile_warning_desc));
                    edit_description.requestFocus();
                }


                // Add recipe to database or perform any other relevant action here

                // Create a new user with a first and last name
                Map<String, Object> updated_info = new HashMap<>();
                updated_info.put("name", name);
                updated_info.put("profile_pic", profile_pic);
                updated_info.put("description", description);



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
                Toast.makeText(getApplicationContext(), "Profile successfully created", Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(EditProfileActivity.this, SplashScreen.class);
                startActivity(mainIntent);

                // Clear input fields
                edit_name.setText("");
                edit_description.setText("");
            }
        });

        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the gallery intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri imageUri  = data.getData();
            uploadImageToFirestore(imageUri);

        }
    }

    private void uploadImageToFirestore(Uri imageUri) {
        // Create a storage reference for the image file
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String ts = s.format(new Date());
        StorageReference storageRef = storage.getReference().child("profile_images/" + user.getUid() + ts + ".jpg");

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

                                        profile_pic = uri.toString();

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display an error message
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
