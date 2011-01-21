
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public boolean[][] array;
    public Sensor[][] sensorArray;

    RailMap() {
    }
    //array-grejen

    public Sensor findSensor(Point p) {
        return points.get(p);
    }

    public void parse(File file) {
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.err.println("file not found: " + ex);
        }

        if (!sc.nextLine().trim().equals("TrainLineFile 2")) {
            System.err.println("not train file!!!!!!");
        }


        int width = sc.nextInt(), height = sc.nextInt();
        array = new boolean[width][height];
        sensorArray = new Sensor[width][height];
        sc.nextLine(); //remove eempty dimensions-line

        while (sc.hasNext()) {
            String line = sc.nextLine().trim();
            if (line.equals(".")) {
                System.err.println("Parse complete!");
                break;
            }

            String[] lines = line.split(" ");

            if(lines[lines.length - 1].equals("station")){
                continue;
            }
            System.out.println("line = " + line);
            int x = Integer.parseInt(lines[1]);
            int y = Integer.parseInt(lines[2]);
            if (lines[0].equals("R")) {
                int numRails = Integer.parseInt(lines[3]);
                boolean isSensor = lines[4+numRails].equals("Sensor");

                array[x][y] = true;
                sensorArray[x][y] = isSensor ? new Sensor(new Point(x, y)) : null;
            } 
            else {
            }
        }
        // RegExp attempt
        /*
        Pattern railPattern =
        Pattern.compile("R\\s+(\\d+)\\s+(\\d+)\\s+\\d+\\s+.+(NoSensor|Sensor)\\s*$");

        while (sc.hasNext()) {
        String line = sc.nextLine().trim();
        if (line.equals(".")) {
        break;
        }

        Matcher m = railPattern.matcher(line);

        while(m.find()){
        System.out.println(m.group(1));
        System.out.println(m.group(2));
        System.out.println(m.group(3));
        //Integer.parseInt()
        }
        }
         *
         */
    }
}
