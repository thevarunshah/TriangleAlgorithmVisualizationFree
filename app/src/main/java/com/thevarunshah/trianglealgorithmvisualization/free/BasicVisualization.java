package com.thevarunshah.trianglealgorithmvisualization.free;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;

public class BasicVisualization extends AppCompatActivity {

    protected static boolean verticesDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_visualization_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RelativeLayout basicVisualization = (RelativeLayout) findViewById(R.id.basic_visualization_activity);
        Snackbar.make(basicVisualization, "Draw vertices and then press done above.", Snackbar.LENGTH_LONG).show();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1000, 1000);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        basicVisualization.addView(new BasicDrawingView(this), layoutParams);

        verticesDone = false;
        BasicDrawingView.clearLists();
        BasicDrawingView.resetSession();

        final Button saveImageButton = (Button) findViewById(R.id.save_image_button);
        saveImageButton.setAlpha(0.5f);
        saveImageButton.setClickable(false);
        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBuyDialog();
            }
        });

        final Button verticesDoneButton = (Button) findViewById(R.id.verticies_done_button);
        verticesDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!verticesDone) {
                    verticesDone = true;
                    verticesDoneButton.setAlpha(.5f);
                    verticesDoneButton.setClickable(false);
                    Snackbar.make(basicVisualization, "Draw a p and then various p-bar's to visualize!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        final Button resetPButton = (Button) findViewById(R.id.reset_p_button);
        resetPButton.setAlpha(0.5f);
        resetPButton.setClickable(false);
        resetPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicDrawingView.p = null;
                BasicDrawingView.pBars.clear();
                BasicDrawingView.previousPBarsSizes.clear();
                BasicDrawingView.updateCanvas(false);
                resetPButton.setAlpha(0.5f);
                resetPButton.setClickable(false);
                saveImageButton.setAlpha(0.5f);
                saveImageButton.setClickable(false);
            }
        });

        final Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticesDone = false;
                verticesDoneButton.setAlpha(1f);
                verticesDoneButton.setClickable(true);
                BasicDrawingView.clearLists();
                BasicDrawingView.resetSession();
                saveImageButton.setAlpha(.5f);
                saveImageButton.setClickable(false);
                resetPButton.setAlpha(0.5f);
                resetPButton.setClickable(false);
            }
        });

        final Spinner xCoord = (Spinner) findViewById(R.id.xcoord_input);
        final Spinner yCoord = (Spinner) findViewById(R.id.ycoord_input);
        Button addPoint = (Button) findViewById(R.id.addpoint_button);
        addPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float xValue = Float.valueOf(String.valueOf(xCoord.getSelectedItem()));
                float yValue = Float.valueOf(String.valueOf(yCoord.getSelectedItem()));
                if(!verticesDone) {
                    BasicDrawingView.addVertex(xValue, yValue);
                }
                else if(verticesDone && BasicDrawingView.p == null){
                    BasicDrawingView.updateP(xValue, yValue);
                    BasicDrawingView.previousPBarsSizes.add(BasicDrawingView.pBars.size());
                    resetPButton.setAlpha(1f);
                    resetPButton.setClickable(true);
                    saveImageButton.setAlpha(1f);
                    saveImageButton.setClickable(true);
                }
                else{
                    BasicDrawingView.runAlgo(new Point(xValue, yValue));
                    BasicDrawingView.updateCanvas(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.basic_visualization_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disply_bisectors:
                displayBuyDialog();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * prompt user to buy the app
     */
    private void displayBuyDialog(){

        //inflate layout with customized alert dialog view
        LayoutInflater layoutInflater = LayoutInflater.from(BasicVisualization.this);
        final View dialog = layoutInflater.inflate(R.layout.info_dialog, null);
        final AlertDialog.Builder infoDialogBuilder = new AlertDialog.Builder(BasicVisualization.this);

        //customize alert dialog and set its view
        infoDialogBuilder.setTitle("Paid Feature");
        infoDialogBuilder.setIcon(R.drawable.ic_info_black_24dp);
        infoDialogBuilder.setView(dialog);

        //fetch textview and set its text
        final TextView message = (TextView) dialog.findViewById(R.id.info_dialog);
        message.setText(R.string.purchase_message);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        //set up actions for dialog buttons
        infoDialogBuilder.setPositiveButton("BUY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {

                String appPackageName = "com.thevarunshah.trianglealgorithmvisualization";
                Intent i = new Intent(Intent.ACTION_VIEW);
                try{
                    i.setData(Uri.parse("market://details?id=" + appPackageName));
                    startActivity(i);
                } catch(ActivityNotFoundException e){
                    try{
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                        startActivity(i);
                    } catch (ActivityNotFoundException e2){
                        Snackbar errorBar = Snackbar.make(findViewById(R.id.basic_visualization_activity),
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
