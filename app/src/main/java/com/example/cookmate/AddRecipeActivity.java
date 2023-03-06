package com.example.cookmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText etTitle, etIngredients, etInstructions;
    private Button btnAddRecipe, btnAddImage;

    private ListView lvIngredients;
    private ImageView ivAddImage;

    private static final int PICK_IMAGE = 1;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        etTitle = findViewById(R.id.et_title);
        etIngredients = findViewById(R.id.et_ingredients);
        etInstructions = findViewById(R.id.et_instructions);
        btnAddRecipe = findViewById(R.id.btn_add_recipe);
        btnAddImage = findViewById(R.id.btn_add_image);
        ivAddImage = findViewById(R.id.iv_add_image);




        // Add click listener to the "Add Recipe" button
        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                new_recipe.put("images", selectedImageUris);
                new_recipe.put("timestamp" , currentDateTime);


                db.collection("recipes").document(title)
                        .set(new_recipe)
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
                Toast.makeText(getApplicationContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
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
                if (selectedImageUris.size() < 5) {
                    // Launch photo picker
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE);
                } else {
                    Toast.makeText(getApplicationContext(), "You can only select up to 5 images", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
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
                ivAddImage.setImageURI(selectedImageUris.get(i));
            } else if (i == 1) {
                // Show the second selected image in the first additional ImageView
                ImageView ivAddImage1 = findViewById(R.id.iv_add_image);
                ivAddImage1.setVisibility(View.VISIBLE);
                ivAddImage1.setImageURI(selectedImageUris.get(i));
            } else if (i == 2) {
                // Show the third selected image in the second additional ImageView
                ImageView ivAddImage2 = findViewById(R.id.iv_add_image2);
                ivAddImage2.setVisibility(View.VISIBLE);
                ivAddImage2.setImageURI(selectedImageUris.get(i));
            } else if (i == 3) {
                // Show the fourth selected image in the third additional ImageView
                ImageView ivAddImage3 = findViewById(R.id.iv_add_image3);
                ivAddImage3.setVisibility(View.VISIBLE);
                ivAddImage3.setImageURI(selectedImageUris.get(i));
            } else if (i == 4) {
                // Show the fifth selected image in the fourth additional ImageView
                ImageView ivAddImage4 = findViewById(R.id.iv_add_image4);
                ivAddImage4.setVisibility(View.VISIBLE);
                ivAddImage4.setImageURI(selectedImageUris.get(i));
            }
        }
    }

}

