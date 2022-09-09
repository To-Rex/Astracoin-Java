package app.astrum.astrocoinuz.classes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;

import app.astrum.astrocoinuz.R;
import app.astrum.astrocoinuz.fragments.FragmentSample;

public class Sample extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        FragmentSample fragmentSample = new FragmentSample();
        fragmentTransaction.replace(R.id.fragmentlayout, fragmentSample);
        fragmentTransaction.commit();
        isOnline();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomDialog);
        builder.setTitle("Exit");
        builder.setIcon(R.drawable.ic_felid);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", (dialog, which) -> finish()
        );builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    public void isOnline() {
        if (isNetworkConnected()) {
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    this::isOnline, 5000);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
            builder.setTitle("No Internet Connection");
            builder.setIcon(R.drawable.ic_felid);
            builder.setCancelable(false);
            builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            builder.setNegativeButton("Retry", (dialog, which) -> {
                isOnline();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}