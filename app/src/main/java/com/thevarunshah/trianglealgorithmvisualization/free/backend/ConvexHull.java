package com.thevarunshah.trianglealgorithmvisualization.free.backend;

import java.util.ArrayList;
import java.util.List;

public class ConvexHull {

    public static List<Point> quickHull(List<Point> allPoints) {

        List<Point> points = new ArrayList<>();
        for(Point p : allPoints){
            points.add(p);
        }
        List<Point> convexHull = new ArrayList<>();
        if (points.size() < 3) {
            return points;
        }

        int minPoint = -1, maxPoint = -1;
        float minX = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        for (int i = 0; i < points.size(); i++){

            if (points.get(i).getX() < minX){
                minX = points.get(i).getX();
                minPoint = i;
            }
            if (points.get(i).getX() > maxX){
                maxX = points.get(i).getX();
                maxPoint = i;
            }
        }
        Point A = points.remove(minPoint);
        if(maxPoint >= minPoint){
            maxPoint--;
        }
        Point B = points.remove(maxPoint);
        convexHull.add(A);
        convexHull.add(B);

        ArrayList<Point> leftSet = new ArrayList<>();
        ArrayList<Point> rightSet = new ArrayList<>();
        for (Point p : points){
            if (pointLocation(A, B, p) == -1) {
                leftSet.add(p);
            }
            else {
                rightSet.add(p);
            }
        }
        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);

        return convexHull;
    }

    private static float distance(Point A, Point B, Point C) {

        float ABx = B.getX() - A.getX();
        float ABy = B.getY() - A.getY();
        float num = ABx * (A.getY() - C.getY()) - ABy * (A.getX() - C.getX());
        if (num < 0) {
            num = -num;
        }
        return num;
    }

    private static void hullSet(Point A, Point B, ArrayList<Point> set, List<Point> hull) {

        int insertPosition = hull.indexOf(B);
        if (set.size() == 0) {
            return;
        }
        if (set.size() == 1){
            Point p = set.remove(0);
            hull.add(insertPosition, p);
            return;
        }
        float dist = Integer.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++){
            Point p = set.get(i);
            float distance = distance(A, B, p);
            if (distance > dist){
                dist = distance;
                furthestPoint = i;
            }
        }
        Point P = set.remove(furthestPoint);
        hull.add(insertPosition, P);

        // Determine who's to the left of AP
        ArrayList<Point> leftSetAP = new ArrayList<>();
        for (Point m : set){
            if (pointLocation(A, P, m) == 1){
                leftSetAP.add(m);
            }
        }

        // Determine who's to the left of PB
        ArrayList<Point> leftSetPB = new ArrayList<>();
        for (Point m : set){
            if (pointLocation(P, B, m) == 1){
                leftSetPB.add(m);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);

    }

    private static int pointLocation(Point A, Point B, Point P) {

        float cp1 = (B.getX()-A.getX()) * (P.getY()-A.getY()) - (B.getY()-A.getY()) * (P.getX()-A.getX());
        return cp1 > 0 ? 1 : -1;
    }
}
