package com.example.cookmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

//import com.example.socialmediaapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText etTitle, etIngredients, etInstructions;
    private Button btnAddRecipe;
    private ImageButton btnAddImage;

    private ListView lvIngredients;
    private ImageView ivAddImage;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private static final String TAG = "MyActivity";

    public String image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        etTitle = findViewById(R.id.et_title);
        etIngredients = findViewById(R.id.et_ingredients);
        etInstructions = findViewById(R.id.et_instructions);
        btnAddRecipe = findViewById(R.id.btn_add_recipe);
        btnAddImage = findViewById(R.id.btn_add_image);



        // Add click listener to the "Add Recipe" button
        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString().trim();
                String ingredients = etIngredients.getText().toString().trim();
                String instructions = etInstructions.getText().toString().trim();


                // Validate user input
                if (title.isEmpty()) {
                    etTitle.setError("Please enter a title");
                    etTitle.requestFocus();
                    return;
                }

                if (ingredients.isEmpty()) {
                    etIngredients.setError("Please enter ingredients");
                    etIngredients.requestFocus();
                    return;
                }

                if (instructions.isEmpty()) {
                    etInstructions.setError("Please enter instructions");
                    etInstructions.requestFocus();
                    return;
                }

                // Add recipe to database or perform any other relevant action here

                //We will want to save timestamp too
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date()); // Find todays date


                // Create a new user with a first and last name
                Map<String, Object> new_recipe = new HashMap<>();
                new_recipe.put("title", title);
                new_recipe.put("ingredients", ingredients);
                new_recipe.put("instructions", instructions);
                //new_recipe.put("image", image_uri);
                new_recipe.put("timestamp" , currentDateTime);
                HomeFragment.addRecipe(new_recipe);

                Log.w(TAG, "New Recipe =>" + new_recipe);
                db.collection("recipes").document(title)
                        .set(new_recipe)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Toast.makeText(getApplicationContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                Toast.makeText(getApplicationContext(), "Recipe NOT added successfully", Toast.LENGTH_SHORT).show();
                            }
                        });


                // Display success message
                finish(); // This will close the current activity and return to the previous one

                // Clear input fields
                etTitle.setText("");
                etIngredients.setText("");
                etInstructions.setText("");
            }
        });
        // Add click listener to the "Add Image" button
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String title = etTitle.getText().toString().trim();
        StorageReference storageRef = storage.getReference().child("recipe_images/" + title + ".jpg");

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
                                        String title = etTitle.getText().toString().trim();
                                        Log.d(TAG, "MENSAJE PRUEBA" + title);
                                        Log.d(TAG, "MENSAJE PRUEBA IMAGEN" + uri.toString());


                                        String image_uri = uri.toString();
                                        db.collection("recipes").document(title).update("image",image_uri );

                                        //db.collection("recipes").document(title).set("images", );
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display an error message
                        Toast.makeText(AddRecipeActivity.this, "Failed to update image", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}

