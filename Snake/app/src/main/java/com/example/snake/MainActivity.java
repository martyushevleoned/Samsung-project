package com.example.snake;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        setContentView(new GameView(this));
    }

    static int maxScore = 0;
    static String SAVED_NUM = "NUMBER";
    static SharedPreferences sharedPreferences;

    static void saveData(Context context) {
        sharedPreferences = context.getSharedPreferences(SAVED_NUM, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVED_NUM, Integer.parseInt(String.valueOf(maxScore)));
        editor.apply();
    }

    static void loadData(Context context) {
        sharedPreferences = context.getSharedPreferences(SAVED_NUM, MODE_PRIVATE);
        maxScore = sharedPreferences.getInt(SAVED_NUM, 0);
    }
}
