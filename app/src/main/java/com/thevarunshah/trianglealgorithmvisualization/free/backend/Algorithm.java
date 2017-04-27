package com.thevarunshah.trianglealgorithmvisualization.free.backend;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    public static void run(Point p, List<Point> vertices, List<Point> pBar, boolean basic){

        int pivotIndex = findPivotIndex(p, vertices, pBar);
        if(pivotIndex == -1){
            return;
        }

        List<Point> pivots = new ArrayList<>();
        pivots.add(vertices.get(pivotIndex));
        pBar.add(calculateNewPBar(pivotIndex, p, vertices, pBar));
        runInternal(p, vertices, pivots, pBar, basic);
    }

    private static void runInternal(Point p, List<Point> vertices, List<Point> pivots, List<Point> pBar, boolean basic){
        double tolerance = .01;
        if(getDistance(p, pBar.get(pBar.size()-1)) < (tolerance * getDistance(p, pivots.get(pivots.size()-1)))) {
            return;
        }
        int pivotIndex = findPivotIndex(p, vertices, pBar);
        if(pivotIndex == -1){
            return;
        }

        pivots.add(vertices.get(pivotIndex));
        pBar.add(calculateNewPBar(pivotIndex, p, vertices, pBar));
        if(basic || (!basic && pBar.size() < 11)) {
            runInternal(p, vertices, pivots, pBar, basic);
        }
    }

    private static double getDistance(Point p1, Point p2){
        return Math.sqrt(Math.pow(p2.getX()-p1.getX(), 2) + Math.pow(p2.getY()-p1.getY(), 2));
    }

    private static int findPivotIndex(Point p, List<Point> vertices, List<Point> pBar){

        int index = -1;
        double maxDist = 0;
        for(int i = 0; i < vertices.size(); i++){
            Point v = vertices.get(i);
            double dist = getDistance(pBar.get(pBar.size()-1), v) - getDistance(p, v);
            if(dist >= 0){
                if(index == -1) {
                    maxDist = dist;
                    index = i;
                }
                else if(dist > maxDist){
                    maxDist = dist;
                    index = i;
                }
            }
        }

        return index;
    }

    private static Point calculateNewPBar(int pivotIndex, Point p, List<Point> vertices, List<Point> pBar){

        Point v = vertices.get(pivotIndex);
        Point pBarPoint = pBar.get(pBar.size()-1);

        float xDiff = pBarPoint.getX()-v.getX();
        float yDiff = pBarPoint.getY()-v.getY();
        double distance_square = Math.pow(yDiff, 2) + Math.pow(xDiff, 2);
        double step_size = ((yDiff * (p.getX()-v.getX())) - (xDiff * (p.getY()-v.getY()))) / distance_square;

        return new Point((float)(p.getX() - (step_size * yDiff)), (float)(p.getY() + (step_size * xDiff)));
    }
}
