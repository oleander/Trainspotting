
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arash
 */
public class RailMap {
    private Map<Point, Sensor> points;
    //array-grejen

    public Sensor findSensor(Point p){
        return points.get(p);
    }

    public RailMap(Map<Point, Sensor> points) {
        this.points = points;
    }
    

    private void parse(File file){
        Scanner sc = null  ;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.err.println("file not found: " + ex);
        }

        while(sc.hasNext()){
            // ... 
        }
    }

}
