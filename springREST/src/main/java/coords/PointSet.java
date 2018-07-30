package coords;

import triangulation.DelaunayTriangulator;
import triangulation.NotEnoughPointsException;
import triangulation.Vector2D;
import moduleArea.Map;

import java.util.ArrayList;
import java.util.List;

public class PointSet {

    List<Vector2D> pointSet = new ArrayList<>();

    public double setPoint(List<Map> dataset){
        try {
                for(int i=0;i<dataset.size();i++)
                {
                    pointSet.add(new Vector2D(dataset.get(i).latitude,dataset.get(i).longitude,dataset.get(i).attitude));
                }

            DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(pointSet);
            double sum = delaunayTriangulator.triangulate();
            return sum;

        }
        catch (NotEnoughPointsException e1) { return 0;
        }

        }
    }
