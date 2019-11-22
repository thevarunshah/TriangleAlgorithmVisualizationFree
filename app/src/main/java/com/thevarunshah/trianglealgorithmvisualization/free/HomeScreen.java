package com.thevarunshah.trianglealgorithmvisualization.free;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_activity);

        Button basicVisualization = (Button) findViewById(R.id.basic_visualization);
        basicVisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeScreen.this, BasicVisualization.class);
                startActivity(i);
            }
        });

        Button fullGradientVisualization = (Button) findViewById(R.id.gradient_visualization);
        fullGradientVisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeScreen.this, GradientVisualization.class);
                startActivity(i);
            }
        });

        Button about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAboutDialog();
            }
        });
    }

    /**
     * display app information and prompt them to rate it.
     */
    private void displayAboutDialog(){

        //inflate layout with customized alert dialog view
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        final View dialog = layoutInflater.inflate(R.layout.info_dialog, null);
        final AlertDialog.Builder infoDialogBuilder = new AlertDialog.Builder(HomeScreen.this);

        //customize alert dialog and set its view
        infoDialogBuilder.setTitle("App Info");
        infoDialogBuilder.setIcon(R.drawable.ic_info_black_24dp);
        infoDialogBuilder.setView(dialog);

        //fetch textview and set its text
        final TextView message = (TextView) dialog.findViewById(R.id.info_dialog);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        //set up actions for dialog buttons
        infoDialogBuilder.setPositiveButton("RATE APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {

                String appPackageName = getApplicationContext().getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try{
                    i.setData(Uri.parse("market://details?id=" + appPackageName));
                    startActivity(i);
                } catch(ActivityNotFoundException e){
                    try{
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                        startActivity(i);
                    } catch (ActivityNotFoundException e2){
                        Snackbar errorBar = Snackbar.make(findViewById(R.id.homescreen_activity),
                                "Could not launch the Google Play app.", Snackbar.LENGTH_SHORT);
                        errorBar.show();
                    }
                }
            }
        });
        infoDialogBuilder.setNegativeButton("DISMISS", null);

        //create and show the dialog
        AlertDialog infoDialog = infoDialogBuilder.create();
        infoDialog.show();
    }
}
