package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.recipe_app.databinding.ActivityRecipeDetailsBinding;
import com.example.recipe_app.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;

public class RecipeDetailsActivity extends AppCompatActivity {
    ActivityRecipeDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init(){
        Recipe recipe= (Recipe) getIntent().getSerializableExtra("recipe");
        binding.tvName.setText(recipe.getName());
        binding.tcCategory.setText(recipe.getCategory());
        binding.tvDescription.setText(recipe.getDescription());
        binding.tvCalories.setText(String.format("%s Calories", recipe.getCalories()));
        Glide
                .with(RecipeDetailsActivity.this)
                .load(recipe.getImage())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.imgRecipe);
        if (recipe.getAuthorId().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {
            binding.imgEdit.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            binding.imgEdit.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.GONE);
        }
        binding.imgEdit.setOnClickListener(view -> {
            Intent intent = new Intent(binding.getRoot().getContext(), AddRecipeActivity.class);
            intent.putExtra("recipe", recipe);
            intent.putExtra("isEdit", true);
            binding.getRoot().getContext().startActivity(intent);
        });

    }

}