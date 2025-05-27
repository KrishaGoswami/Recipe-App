package com.example.recipe_app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.recipe_app.databinding.ActivityAddRecipeBinding;
import com.example.recipe_app.models.Category;
import com.example.recipe_app.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddRecipeActivity extends AppCompatActivity {
    ActivityAddRecipeBinding binding;
    private ProgressDialog dialog;
    boolean isEdit;
    String recipeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadCategories();
        binding.btnAddRecipe.setOnClickListener(view -> {
            getData();
        });
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            editRecipe();
        }
    }
    private void editRecipe() {
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        recipeId = recipe.getId();
        binding.etRecipeName.setText(recipe.getName());
        binding.etDescription.setText(recipe.getDescription());
        binding.etCookingTime.setText(recipe.getTime());
        binding.etCategory.setText(recipe.getCategory());
        binding.etCalories.setText(recipe.getCalories());
        Glide
                .with(binding.getRoot().getContext())
                .load(recipe.getImage())
                .centerCrop()
                .placeholder(R.drawable.img)
                .into(binding.imgRecipe);

        binding.btnAddRecipe.setText("Update Recipe");
    }

    private void loadCategories() {
        List<String> categories = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        binding.etCategory.setAdapter(adapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categories.add(dataSnapshot.getValue(Category.class).getName());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getData() {
        final String[] url = {""};
        //1. Fetch data from user
        String recipeName = Objects.requireNonNull(binding.etRecipeName.getText()).toString();
        String recipeDescription = Objects.requireNonNull(binding.etDescription.getText()).toString();
        String cookingTime = Objects.requireNonNull(binding.etCookingTime.getText()).toString();
        String recipeCategory = binding.etCategory.getText().toString();
        String calories = Objects.requireNonNull(binding.etCalories.getText()).toString();

        // 2. We will validate the data.
        if (recipeName.isEmpty()) {
            binding.etRecipeName.setError("Please enter Recipe Name");
        } else if (recipeDescription.isEmpty()) {
            binding.etDescription.setError("Please enter Recipe Description");
        } else if (cookingTime.isEmpty()) {
            binding.etCookingTime.setError("Please enter Cooking Time");
        } else if (recipeCategory.isEmpty()) {
            binding.etCategory.setError("Please enter Recipe Category");
        } else if (calories.isEmpty()) {
            binding.etCalories.setError("Please enter Calories");
        } else {

            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Recipe...");
            dialog.setCancelable(false);
            dialog.show();
            Recipe recipe = new Recipe(recipeName, recipeDescription, cookingTime, recipeCategory, calories, "", FirebaseAuth.getInstance().getUid());
            saveDataInDataBase(recipe,url[0]);
        }
    }
    private void saveDataInDataBase(Recipe recipe, String url) {
        recipe.setImage(url);
        //  We will save the recipe object in the firebase database.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recipes");
        if (isEdit) {
            recipe.setId(recipeId);
            reference.child(recipe.getId()).setValue(recipe).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(AddRecipeActivity.this, "Recipe Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRecipeActivity.this, "Error in updating recipe", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String id = reference.push().getKey();
            recipe.setId(id);
            if (id != null) {
                reference.child(id).setValue(recipe).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(AddRecipeActivity.this, "Recipe Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddRecipeActivity.this, "Error in adding recipe", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}