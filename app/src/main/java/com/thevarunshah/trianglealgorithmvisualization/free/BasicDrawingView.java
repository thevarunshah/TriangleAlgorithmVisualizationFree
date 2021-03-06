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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.Algorithm;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.PaintColors;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class BasicDrawingView extends SurfaceView {

    protected static List<Point> vertices = new ArrayList<>();
    protected static Point p = null;
    protected static List<Point> pBars = new ArrayList<>();
    protected static List<Integer> previousPBarsSizes = new ArrayList<>();

    private static SurfaceHolder surfaceHolder = null;
    private final static Paint blackPaint = PaintColors.getBlack();
    private final static Paint whitePaint = PaintColors.getWhite();
    private final static Paint redPaint = PaintColors.getRed();
    private final static Paint greenPaint = PaintColors.getGreen();
    private final static Paint bluePaint = PaintColors.getBlue();

    public BasicDrawingView(Context context) {
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
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (surfaceHolder.getSurface().isValid()) {
                if(!BasicVisualization.verticesDone) {
                    addVertex(event.getX(), event.getY());
                }
                else if(BasicVisualization.verticesDone && p == null){
                    updateP(event.getX(), event.getY());
                    previousPBarsSizes.add(pBars.size());
                    Button resetPButton = (Button) getRootView().findViewById(R.id.reset_p_button);
                    resetPButton.setAlpha(1f);
                    resetPButton.setClickable(true);
                    Button saveImageButton = (Button) getRootView().findViewById(R.id.save_image_button);
                    saveImageButton.setAlpha(1f);
                    saveImageButton.setClickable(true);
                }
                else{
                    runAlgo(new Point(event.getX(), event.getY()));
                    updateCanvas(true);
                }
            }
        }
        return false;
    }

    protected static void addVertex (float x, float y){
        vertices.add(new Point(x, y));
        updateCanvas(false);
    }

    protected static void updateP (float x, float y){
        p = new Point(x, y);
        updateCanvas(false);
    }

    protected static void updateCanvas(boolean fullUpdate){
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawPaint(whitePaint);
        if(fullUpdate && p != null && pBars.size() != 0){
            drawLines(canvas);
            for (Point p : pBars) {
                canvas.drawCircle(p.getX(), p.getY(), 5, bluePaint);
            }
            for (int i = 0; i < previousPBarsSizes.size()-1; i++) {
                int size = previousPBarsSizes.get(i);
                canvas.drawCircle(pBars.get(size).getX(), pBars.get(size).getY(), 5, greenPaint);
            }
        }
        for (Point p : vertices) {
            canvas.drawCircle(p.getX(), p.getY(), 10, blackPaint);
        }
        if(p != null) {
            canvas.drawCircle(p.getX(), p.getY(), 10, redPaint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    protected static void runAlgo(Point newPBar){
        pBars.add(newPBar);
        Algorithm.run(p, vertices, pBars, true);
        previousPBarsSizes.add(pBars.size());
    }

    private static void drawLines(Canvas canvas){
        for(int i = pBars.size()-1; i > 0; i--){
            if(!previousPBarsSizes.contains(i)) {
                canvas.drawLine(pBars.get(i).getX(), pBars.get(i).getY(), pBars.get(i-1).getX(), pBars.get(i-1).getY(), blackPaint);
            }
        }
        for(int i = 1; i < previousPBarsSizes.size(); i++){
            int size = previousPBarsSizes.get(i);
            canvas.drawLine(p.getX(), p.getY(), pBars.get(size-1).getX(), pBars.get(size-1).getY(), redPaint);
            float dx = pBars.get(size-1).getX() - p.getX();
            float dy = pBars.get(size-1).getY() - p.getY();
            float ox = p.getX() + (dx - dy) / 2;
            float oy = p.getY() + (dx + dy) / 2;
            if(BasicVisualization.displayBisectors) {
                canvas.drawLine(ox, oy, ox + dy, oy - dx, redPaint);
            }
        }
    }

    static class SaveImageTask extends AsyncTask<Void, Void, Bitmap> {

        private Context context;
        private Button saveImage;

        private Point taskPoint = null;
        private List<Point> taskVertices = new ArrayList<>();
        private List<Point> taskPBars = new ArrayList<>();
        private List<Integer> taskPreviousPBarsSizes = new ArrayList<>();
        private boolean displayBisectors;

        SaveImageTask(Context context, Button saveImage){
            this.context = context;
            this.saveImage = saveImage;
            if(p != null) {
                this.taskPoint = new Point(p.getX() * 5, p.getY() * 5);
            }
            for(Point v : vertices){
                taskVertices.add(new Point(v.getX()*5, v.getY()*5));
            }
            for(Point p : pBars){
                taskPBars.add(new Point(p.getX()*5, p.getY()*5));
            }
            for(Integer i : previousPBarsSizes){
                taskPreviousPBarsSizes.add(i);
            }
            this.displayBisectors = BasicVisualization.displayBisectors;
        }

        @Override
        protected Bitmap doInBackground(Void... nothing) {

            if(taskPoint == null){
                return null;
            }

            Bitmap bitmap = Bitmap.createBitmap(5000, 5000, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawPaint(whitePaint);

            //draw lines
            blackPaint.setStrokeWidth(2);
            for(int i = taskPBars.size()-1; i > 0; i--){
                if(!taskPreviousPBarsSizes.contains(i)) {
                    canvas.drawLine(taskPBars.get(i).getX(), taskPBars.get(i).getY(), taskPBars.get(i-1).getX(), taskPBars.get(i-1).getY(), blackPaint);
                }
            }
            blackPaint.setStrokeWidth(1);
            for(int i = 1; i < taskPreviousPBarsSizes.size(); i++){
                int size = taskPreviousPBarsSizes.get(i);
                canvas.drawLine(taskPoint.getX(), taskPoint.getY(), taskPBars.get(size-1).getX(), taskPBars.get(size-1).getY(), redPaint);
                float dx = taskPBars.get(size-1).getX() - taskPoint.getX();
                float dy = taskPBars.get(size-1).getY() - taskPoint.getY();
                float ox = taskPoint.getX() + (dx - dy) / 2;
                float oy = taskPoint.getY() + (dx + dy) / 2;
                if(displayBisectors) {
                    canvas.drawLine(ox, oy, ox + dy, oy - dx, redPaint);
                }
            }

            for (Point p : taskPBars) {
                canvas.drawCircle(p.getX(), p.getY(), 25, bluePaint);
            }
            for (int i = 0; i < taskPreviousPBarsSizes.size()-1; i++) {
                int size = taskPreviousPBarsSizes.get(i);
                canvas.drawCircle(taskPBars.get(size).getX(), taskPBars.get(size).getY(), 25, greenPaint);
            }
            for (Point p : taskVertices) {
                canvas.drawCircle(p.getX(), p.getY(), 50, blackPaint);
            }
            canvas.drawCircle(taskPoint.getX(), taskPoint.getY(), 50, redPaint);
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

                if(BasicVisualization.verticesDone) {
                    saveImage.setAlpha(1f);
                    saveImage.setClickable(true);
                }
            }
        }
    }

    protected static void clearLists(){
        previousPBarsSizes.clear();
        pBars.clear();
        vertices.clear();
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
