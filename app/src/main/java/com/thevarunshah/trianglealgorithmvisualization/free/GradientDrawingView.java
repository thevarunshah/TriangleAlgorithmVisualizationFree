package com.thevarunshah.trianglealgorithmvisualization.free;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thevarunshah.trianglealgorithmvisualization.free.backend.Algorithm;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.PaintColors;
import com.thevarunshah.trianglealgorithmvisualization.free.backend.Point;

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
