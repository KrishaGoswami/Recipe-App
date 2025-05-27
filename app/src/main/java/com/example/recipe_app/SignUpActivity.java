package com.example.recipe_app;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.recipe_app.databinding.ActivitySignUpBinding;
import com.example.recipe_app.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSignup.setOnClickListener(view->signup());
        binding.tvLogin.setOnClickListener(view->finish());
        }

    private void signup() {
    String name= Objects.requireNonNull(binding.etName.getText()).toString().trim();
    String email= Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
    String password= Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
    if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
        Toast.makeText(this,"please enter your name, email and password",Toast.LENGTH_SHORT).show();
    }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //startActivity(new Intent(this,MainActivity.class));
        Toast.makeText(this,"Please enter a valid email address", Toast.LENGTH_SHORT).show();
    }else if(password.length()<6){
        Toast.makeText(this,"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
    }else{
        createNewUser(name, email, password);
        }
    }

    private void createNewUser(String name, String email, String password) {
        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    // Update display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            saveUserToDatabase();
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to update user profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;
        String defaultProfileImage = getString(R.string.default_profile_image);
        String defaultCoverImage = getString(R.string.default_cover_image);

        String uid = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName(); // or get from EditText
        String email = firebaseUser.getEmail();
        String image = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : defaultProfileImage;
        String cover = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : defaultCoverImage;
        User user = new User(uid, name, email,image,cover);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(uid).setValue(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "User saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}