package com.example.recipe_app;

import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.recipe_app.adapter.RecipeAdapter;
import com.example.recipe_app.databinding.ActivityAddRecipeBinding;
import com.example.recipe_app.databinding.ActivityAllRecipesBinding;
import com.example.recipe_app.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllRecipesActivity extends AppCompatActivity {
    ActivityAllRecipesBinding binding;
    DatabaseReference reference;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reference = FirebaseDatabase.getInstance().getReference("Recipes");
        binding.rvRecipes.setLayoutManager(new GridLayoutManager(this,2));
        binding.rvRecipes.setAdapter(new RecipeAdapter());
        type = getIntent().getStringExtra("type");
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (type.equalsIgnoreCase("category")) {
            filterByCategory();
        } else if (type.equalsIgnoreCase("search")) {
            loadByRecipes();
        } else {
            loadAllRecipes();
        }
    }

    private void loadAllRecipes() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                Collections.shuffle(recipes);
                RecipeAdapter adapter = (RecipeAdapter) binding.rvRecipes.getAdapter();
                if (adapter != null) {
                    adapter.setRecipeList(recipes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void loadByRecipes() {
        String query = getIntent().getStringExtra("query");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe.getName().toLowerCase().contains(query.toLowerCase()))
                        recipes.add(recipe);
                }
                RecipeAdapter adapter = (RecipeAdapter) binding.rvRecipes.getAdapter();
                if (adapter != null) {
                    adapter.setRecipeList(recipes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void filterByCategory() {
        String category = getIntent().getStringExtra("category");
        reference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> recipes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                RecipeAdapter adapter = (RecipeAdapter) binding.rvRecipes.getAdapter();
                if (adapter != null) {
                    adapter.setRecipeList(recipes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }
}