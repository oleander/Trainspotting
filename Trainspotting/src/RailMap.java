
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RailMap {

    private int width, height;
    private boolean[][] array;
    private Sensor[][] sensorArray;
    private ArrayList<Point> trainList;


    RailMap(File file) {
        trainList = new ArrayList<Point>();
        parse(file);
    }

    public Sensor findSensor(int x, int y) {
        return sensorArray[x][y];
    }

    private void parse(File file) {
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.err.println("file not found: " + ex);
        }

        if (!sc.nextLine().trim().equals("TrainLineFile 2")) {
            System.err.println("not train file!!!!!!");
        }


        width = sc.nextInt();
        height = sc.nextInt();
        array = new boolean[width][height];
        sensorArray = new Sensor[width][height];
        sc.nextLine(); //remove eempty dimensions-line

        while (sc.hasNext()) {
            String line = sc.nextLine().trim();
            if (line.equals(".")) {
                System.err.println("Parse complete!");
                break;
            }

            String[] sline = line.split(" ");

            if (sline[sline.length - 1].equals("station")) {
                continue;
            }
            int x = Integer.parseInt(sline[1]);
            int y = Integer.parseInt(sline[2]);
            if (sline[0].equals("R")) {
                //int numRails = Integer.parseInt(lines[3]); // not needed now
                boolean isSensor = sline[sline.length - 1].equals("Sensor");

                array[x][y] = true;
                sensorArray[x][y] =
                        isSensor ? new Sensor(new Point(x, y)) : null;
            } else {
                trainList.add(new Point(x, y));
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

    /**
     * Get a array you can search the railway in
     * @return array to search in. It's a copy, so you can edit it
     */
    public int[][] getMinusOneFilledArray() {
        int[][] arr = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                arr[i][j] = array[i][j] ? 1000 : -1;
            }
        }

        return arr;
    }

    public boolean[][] getArray() {
        return array;
    }

    public int getHeight() {
        return height;
    }

    public Sensor[][] getSensorArray() {
        return sensorArray;
    }

    public int getWidth() {
        return width;
    }

    public int getNumTrains(){
        return trainList.size();
    }

    public Point trainStartPos(int id){
        return trainList.get(id-1);
    }
}
