package com.thevarunshah.trianglealgorithmvisualization.free;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.Algorithm;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.ConvexHull;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.PaintColors;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GradientDrawingView extends SurfaceView {

    protected static List<Point> vertices = new ArrayList<>();
    private static Point p = null;
    protected static SparseArray<List<Point>> prevIterationsMap = null;
    protected static List<Point> convexHull = null;

    private static SurfaceHolder surfaceHolder = null;
    private final static Paint blackPaint = PaintColors.getBlack();
    private final static Paint whitePaint = PaintColors.getWhite();
    private final static Paint redPaint = PaintColors.getRed();
    private final static Paint greenPaint = PaintColors.getGreen();

    protected static int checkedItem = R.id.green;

    public GradientDrawingView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.setFixedSize(1000, 1000);
    }

    @Override
    protected void onAttachedToWindow() {
        new Handler().post(new Runnable() {
            public void run() {
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawPaint(whitePaint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        });
        super.onAttachedToWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && surfaceHolder.getSurface().isValid()) {
            if(!GradientVisualization.verticesDone) {
                addVertex(event.getX(), event.getY());
            }
            else{
                updateP(event.getX(), event.getY());
                SparseArray<List<Point>> iterationsMap = runAlgo();
                prevIterationsMap = iterationsMap;
                updateCanvas(iterationsMap);
            }
        }
        return false;
    }

    protected static void updateCanvas(SparseArray<List<Point>> iterationsMap){
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawPaint(whitePaint);
        if(iterationsMap != null){
            for(int i = 0; i < iterationsMap.size(); i++){
                int key = iterationsMap.keyAt(i);
                Paint shade = getShade(key, checkedItem);
                for(Point p : iterationsMap.get(key)){
                    canvas.drawCircle(p.getX(), p.getY(), 5, shade);
                }
            }
        }
        for (Point p : vertices) {
            canvas.drawCircle(p.getX(), p.getY(), 10, blackPaint);
        }
        if(p != null) {
            canvas.drawCircle(p.getX(), p.getY(), 10, checkedItem == R.id.red ? greenPaint : redPaint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    protected static void addVertex (float x, float y){
        vertices.add(new Point(x, y));
        updateCanvas(null);
    }

    protected static void updateP (float x, float y){
        resetSession();
        p = new Point(x, y);
        updateCanvas(null);
    }

    protected static SparseArray<List<Point>> runAlgo(){
        SparseArray<List<Point>> iterationsMap = new SparseArray<>();
        for(float i = 0; i < 1000; i+=5){
            for(float j = 0; j < 1000; j+=5) {
                if(GradientVisualization.displayConvexHull && !contains(new Point(i, j), convexHull)){
                    continue;
                }
                List<Point> pBar = new ArrayList<>();
                pBar.add(new Point(i, j));
                Algorithm.run(p, vertices, pBar, false);
                if(iterationsMap.get(pBar.size()) == null){
                    iterationsMap.put(pBar.size(), new ArrayList<Point>());
                }
                iterationsMap.get(pBar.size()).add(new Point(i, j));
            }
        }
        return iterationsMap;
    }

    static class SaveImageTask extends AsyncTask<Void, Void, Bitmap> {

        private Context context;
        private Button saveImage;

        private Point taskPoint = null;
        private List<Point> taskVertices = new ArrayList<>();
        private boolean displayConvexHull;
        private int checkedItem;

        SaveImageTask(Context context, Button saveImage){
            this.context = context;
            this.saveImage = saveImage;
            if(p != null) {
                this.taskPoint = new Point(p.getX() * 5, p.getY() * 5);
            }
            for(Point v : vertices){
                taskVertices.add(new Point(v.getX()*5, v.getY()*5));
            }
            this.displayConvexHull = GradientVisualization.displayConvexHull;
            this.checkedItem = GradientDrawingView.checkedItem;
        }

        @Override
        protected Bitmap doInBackground(Void... nothing) {

            if(taskPoint == null){
                return null;
            }

            Bitmap bitmap = Bitmap.createBitmap(5000, 5000, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            List<Point> convexHull = ConvexHull.quickHull(taskVertices);
            SparseArray<List<Point>> iterationsMap = new SparseArray<>();
            for(float i = 0; i < 5000; i+=5){
                for(float j = 0; j < 5000; j+=5) {
                    if(displayConvexHull && !contains(new Point(i, j), convexHull)){
                        continue;
                    }
                    List<Point> pBar = new ArrayList<>();
                    pBar.add(new Point(i, j));
                    Algorithm.run(taskPoint, taskVertices, pBar, false);
                    if(iterationsMap.get(pBar.size()) == null){
                        iterationsMap.put(pBar.size(), new ArrayList<Point>());
                    }
                    iterationsMap.get(pBar.size()).add(new Point(i, j));
                }
            }

            canvas.drawPaint(whitePaint);
            for(int i = 0; i < iterationsMap.size(); i++){
                int key = iterationsMap.keyAt(i);
                Paint shade = getShade(key, checkedItem);
                for(Point p : iterationsMap.get(key)){
                    canvas.drawCircle(p.getX(), p.getY(), 5, shade);
                }
            }
            for (Point p : taskVertices) {
                canvas.drawCircle(p.getX(), p.getY(), 50, blackPaint);
            }
            canvas.drawCircle(taskPoint.getX(), taskPoint.getY(), 50, checkedItem == R.id.red ? greenPaint : redPaint);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if(bitmap != null){
                try {
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Triangle Algorithm Visualizations");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir.getAbsolutePath(), "TAV_" + System.currentTimeMillis() + ".png");
                    file.createNewFile();

                    try (OutputStream fOut = new FileOutputStream(file)){
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    }
                    MediaScannerConnection.scanFile(context, new String[] {file.toString()}, null, null);
                } catch (Exception e){
                    Log.i("Error", "Couldn't save image: " + e.getMessage());
                }

                if(GradientVisualization.verticesDone) {
                    saveImage.setAlpha(1f);
                    saveImage.setClickable(true);
                }
            }
        }
    }

    private static boolean contains(Point test, List<Point> convexHull) {
        boolean result = false;
        for (int i = 0, j = convexHull.size()-1; i < convexHull.size(); j = i++) {
            Point pi = convexHull.get(i);
            Point pj = convexHull.get(j);
            float xDiff = pj.getX() - pi.getX();
            float yDiff = pj.getY()- pi.getY();
            if ((pi.getY() > test.getY()) != (pj.getY() > test.getY()) &&
                    (test.getX() < xDiff * (test.getY() - pi.getY()) / yDiff + pi.getX())) {
                result = !result;
            }
        }
        return result;
    }

    private static Paint getShade(int key, int checkedItem){
        switch(checkedItem) {
            case R.id.red:
                return PaintColors.getCustomColor(270 - (15 * key), 0, 0);
            case R.id.blue:
                return PaintColors.getCustomColor(110 - (5 * key), 210 - (10 * key), 270 - (15 * key)); //cyan
            case R.id.yellow:
                return PaintColors.getCustomColor(270 - (15 * key), 270 - (15 * key), 0);
            default:
                return PaintColors.getCustomColor(0, 270 - (15 * key), 0); //green
        }
    }

    protected static void resetSession(){
        p = null;
        Canvas canvas = surfaceHolder.lockCanvas();
        if(canvas == null){
            return;
        }
        canvas.drawPaint(whitePaint);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}
