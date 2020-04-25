package com.example.flappy2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.pm.ActivityInfo.*;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(SCREEN_ORIENTATION_USER_PORTRAIT);
        setContentView(new GameView(this));
    }

    static int saveThis = 0;
    final String SAVED_NUM = "NUMBER";
    SharedPreferences sharedPreferences;

    public static void save(){

    }

    void saveData() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVED_NUM, Integer.parseInt(String.valueOf(saveThis)));
        editor.apply();
    }

    void loadData() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        saveThis = sharedPreferences.getInt(SAVED_NUM, 0);
    }
}