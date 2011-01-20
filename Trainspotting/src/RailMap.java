
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(RailMap.class.getName()).log(Level.SEVERE, null, ex);
        }

        while(sc.hasNext()){
            // ... 
        }
    }

}
