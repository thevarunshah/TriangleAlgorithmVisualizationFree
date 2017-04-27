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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.ConvexHull;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;

import java.util.List;

public class GradientVisualization extends AppCompatActivity {

    protected static boolean verticesDone = false;
    protected static boolean displayConvexHull = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradient_visualization_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RelativeLayout gradientVisualization = (RelativeLayout) findViewById(R.id.gradient_visualization_activity);
        Snackbar.make(gradientVisualization, "Draw vertices and then press done above.", Snackbar.LENGTH_LONG).show();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1000, 1000);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        gradientVisualization.addView(new GradientDrawingView(this), layoutParams);
        verticesDone = false;
        GradientDrawingView.vertices.clear();
        GradientDrawingView.resetSession();

        final Button saveImage = (Button) findViewById(R.id.save_image_button);
        saveImage.setAlpha(0.5f);
        saveImage.setClickable(false);
        saveImage.setOnClickListener(new View.OnClickListener() {
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
                    saveImage.setAlpha(1f);
                    saveImage.setClickable(true);
                    GradientDrawingView.convexHull = ConvexHull.quickHull(GradientDrawingView.vertices);
                    Snackbar.make(gradientVisualization, "Draw various p's to visualize!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        final Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticesDone = false;
                verticesDoneButton.setAlpha(1f);
                verticesDoneButton.setClickable(true);
                GradientDrawingView.vertices.clear();
                GradientDrawingView.resetSession();
                saveImage.setAlpha(.5f);
                saveImage.setClickable(false);
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
                    GradientDrawingView.addVertex(xValue, yValue);
                }
                else{
                    GradientDrawingView.updateP(xValue, yValue);
                    SparseArray<List<Point>> iterationsMap = GradientDrawingView.runAlgo();
                    GradientDrawingView.prevIterationsMap = iterationsMap;
                    GradientDrawingView.updateCanvas(iterationsMap);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gradient_visualization_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.green:
            case R.id.red:
                item.setChecked(true);
                GradientDrawingView.checkedItem = item.getItemId();
                GradientDrawingView.updateCanvas(GradientDrawingView.prevIterationsMap);
                return true;
            case R.id.blue:
            case R.id.yellow:
                displayBuyDialog();
                return true;
            case R.id.convex_hull:
                item.setChecked(!item.isChecked());
                displayConvexHull = !displayConvexHull;
                SparseArray<List<Point>> iterationsMap = GradientDrawingView.runAlgo();
                GradientDrawingView.prevIterationsMap = iterationsMap;
                GradientDrawingView.updateCanvas(iterationsMap);
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
        LayoutInflater layoutInflater = LayoutInflater.from(GradientVisualization.this);
        final View dialog = layoutInflater.inflate(R.layout.info_dialog, null);
        final AlertDialog.Builder infoDialogBuilder = new AlertDialog.Builder(GradientVisualization.this);

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
                        Snackbar errorBar = Snackbar.make(findViewById(R.id.gradient_visualization_activity),
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
