package com.example.recipe_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
// we are using view binding instead of using find view by id
import com.example.recipe_app.databinding.ActivitySplashBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")

public class SplashActivity extends AppCompatActivity {
    private int splashScreenTime=3000; //3sec
    private int timeInterval=100; //0.1sec
    private int progress=0; // 0 to 100 bar
    private Runnable runnable;
    private Handler handler;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashBinding.inflate(getLayoutInflater()); // view binding for splash screen
        setContentView(binding.getRoot());
        binding.progressBar.setMax(splashScreenTime);
        binding.progressBar.setProgress(progress);
        handler=new Handler(Looper.getMainLooper());
        runnable=()->{
                // this code will check splash screen time completed or not
                if(progress<splashScreenTime){
                    progress+=timeInterval;
                    binding.progressBar.setProgress(progress);
                    handler.postDelayed(runnable, timeInterval);
                }else{
                    //runs after splash screen
                      startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                    finish();
                }
            };
        handler.postDelayed(runnable,timeInterval);

    }
}