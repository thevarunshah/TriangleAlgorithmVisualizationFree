package com.thevarunshah.trianglealgorithmvisualization.free;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.VerifyPermissions;

public class BasicVisualization extends AppCompatActivity {

    protected static boolean verticesDone = false;
    protected static boolean displayBisectors = false;

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
                VerifyPermissions.verifyStoragePermissions(BasicVisualization.this);
                saveImageButton.setAlpha(0.5f);
                saveImageButton.setClickable(false);
                BasicDrawingView.SaveImageTask task = new BasicDrawingView.SaveImageTask(basicVisualization.getContext(), saveImageButton);
                task.execute();
                Snackbar.make(basicVisualization, "Your image has been saved!", Snackbar.LENGTH_LONG).show();
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

        VerifyPermissions.verifyStoragePermissions(this);
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
                displayBisectors = !displayBisectors;
                item.setChecked(!item.isChecked());
                BasicDrawingView.updateCanvas(true);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
