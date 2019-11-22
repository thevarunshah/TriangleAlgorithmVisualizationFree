package com.thevarunshah.trianglealgorithmvisualization.free;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.ConvexHull;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.VerifyPermissions;

import java.util.List;

public class GradientVisualization extends AppCompatActivity {

    protected static boolean verticesDone = false;
    protected static boolean displayConvexHull = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradient_visualization_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RelativeLayout gradientVisualization = (RelativeLayout) findViewById(R.id.full_gradient_visualization_activity);
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
                VerifyPermissions.verifyStoragePermissions(GradientVisualization.this);
                saveImage.setAlpha(0.5f);
                saveImage.setClickable(false);
                GradientDrawingView.SaveImageTask task = new GradientDrawingView.SaveImageTask(gradientVisualization.getContext(), saveImage);
                Snackbar.make(gradientVisualization, "Your image is being generated and will be saved soon!", Snackbar.LENGTH_LONG).show();
                task.execute();
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

        VerifyPermissions.verifyStoragePermissions(this);
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
            case R.id.blue:
            case R.id.yellow:
                item.setChecked(true);
                GradientDrawingView.checkedItem = item.getItemId();
                GradientDrawingView.updateCanvas(GradientDrawingView.prevIterationsMap);
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
}
