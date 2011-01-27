
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RailMap {

    private int width, height;
    private int[][] array; //true if walkable
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
        createArray();
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

                //array[x][y] = true;
                int numRails = Integer.parseInt(sline[3]);
                for (int i = 0; i < numRails; i++) {
                    addRail(x, y, sline[4 + i]);
                }

                sensorArray[x][y] =
                        isSensor ? new Sensor(new Point(x, y), this) : null;
            } else {
                trainList.add(new Point(x, y));
            }
        }
    }

    /**
     * Get a array you can search the railway in. Doesn't contain switch-data
     * @return array to search in. It's a copy, so you can edit it
     */
    public boolean[][] getSearchArray() {
        boolean[][] arr =
                new boolean[transformToDetailed(width)][transformToDetailed(height)];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] = array[i][j] > 0; // true if can walk on
            }
        }

        return arr;
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

    public int getNumTrains() {
        return trainList.size();
    }

    public Point trainStartPos(int id) {
        return trainList.get(id - 1);
    }

    public void printAsciiMap() {
        System.err.println("");
        for (int y = 0; y < array[0].length; y++) {
            for (int x = 0; x < array.length; x++) {                
                if (false && sensorArray[x][y] != null) {
                    System.err.print("S");
                } else if (array[x][y] > 0) {
                    System.err.print(array[x][y]);
                } else {
                    System.err.print("#");
                }
            }
            System.err.println("");
        }
    }

    public boolean isCrossing(Point p) {
        return isHavingExactAdjacent(p, 4);
    }

    public boolean isSwitch(Point p) {
        return isHavingExactAdjacent(p, 3);
    }

    public boolean isEnd(Point p) {
        return isHavingExactAdjacent(p, 1);
    }

    private boolean isHavingExactAdjacent(Point p, int wantedAdjacent){
        int x0 = transformToDetailed(p.x);
        int y0 = transformToDetailed(p.y);
        int numAdjacent = 0;
        for (int dir = 0; dir < 4; dir++) {
            int x = x0 + DirectionArrays.xDirs[dir];
            int y = y0 + DirectionArrays.yDirs[dir];
            if (!validDetailedCoordinate(x, y)) {
                continue;
            }
            numAdjacent += array[x][y] > 0 ? 1 : 0;
        }

        return numAdjacent == wantedAdjacent;
    }

    // TODO: this isn't valid for new array type
    public boolean canMoveInDirection(Point from, int dir) {
        int x = transformToDetailed(from.x);
        int y = transformToDetailed(from.y);
        x += DirectionArrays.xDirs[dir];
        y += DirectionArrays.yDirs[dir];
        return validDetailedCoordinate(x, y) && array[x][y] > 0;
    }

    public SearchResult getNextCrossing(Point from, int dir0) {
        return searchForPredicate(from, dir0, new PointCond() {

            public boolean ok(Point p) {
                return isCrossing(p);
            }
        });
    }

    public SearchResult getNextSwitch(Point from, int dir0) {
        return searchForPredicate(from, dir0, new PointCond() {

            public boolean ok(Point p) {
                return isSwitch(p);
            }
        });
    }

    public SearchResult getNextSwitchOrEnd(Point from, int dir0) {
        return searchForPredicate(from, dir0, new PointCond() {

            public boolean ok(Point p) {
                return isSwitch(p) || isEnd(p);
            }
        });
    }

    SearchResult getNextSensor(Point from, int dir0) {
        return searchForPredicate(from, dir0, new PointCond() {

            public boolean ok(Point p) {
                return getSensor(p) != null;
            }
        });
    }


    public Sensor getSensor(Point p) {
        return sensorArray[p.x][p.y];
    }

    private SearchResult searchForPredicate(Point from, int dir, PointCond pc) {
        Point now = new Point(from.x, from.y);
        int dist = 0;
        while (!pc.ok(now) || now.equals(from)) {
            dir = getPrefferedDirection(now, dir);
            //System.err.println(dir);
            if (dir == -1) {
                return null;
            }
            now.moveInDirection(dir);
            dist++;
        }
        return new SearchResult(now, dir, dist);
    }

    private int getPrefferedDirection(Point now, int dir) {
        int[] preferredDirs = {dir, (dir + 1) % 4, (dir - 1 + 4) % 4};
        for (int d : preferredDirs) {
            if (canMoveInDirection(now, d)) {
                return d;
            }
        }
        return -1;
    }

    private boolean validDetailedCoordinate(int x, int y) {
        return !(x <= 0 || y <= 0 || x >= transformToDetailed(width) || y >= transformToDetailed(height));
    }

    private interface PointCond {

        public boolean ok(Point p);
    }

    public int getDirectionTrainCameWith(Point p0, Point p1) {
        int x0 = p0.x, y0 = p0.y, x1 = p1.x, y1 = p1.y;
        System.err.println("bfsing from: (" + x0 + ", " + y0 + ") to (" + x1 + ", " + y1 + ")");
        Queue<Point> queue = new LinkedList<Point>();
        final Point startPoint = new Point(x0, y0);
        queue.add(startPoint);

        boolean[][] canWalkOn = getSearchArray();

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (!canWalkOn[p.x][p.y]) {
                continue;
            }
            canWalkOn[p.x][p.y] = false; //mark visited
            for (int dir = 0; dir < 4; dir++) {
                int x = p.x + DirectionArrays.xDirs[dir];
                int y = p.y + DirectionArrays.yDirs[dir];

                if (x < 0 || x >= canWalkOn.length || y < 0 || y >= canWalkOn[0].length) {
                    continue; // so we don't search outside the bounds
                }

                if (x == x1 && y == y1) {
                    return dir;
                }
                queue.add(new Point(x, y));
            }
        }

        System.err.println("bfs failed!");
        return -123;
    }

    private static int transformToDetailed(int xORy) {
        //return xORy; //old
        return xORy * 2 + 1;
    }

    private static Point transformToDetailed(Point p) {
        return new Point(transformToDetailed(p.x), transformToDetailed(p.y));
    }

    // TODO fix so it works for 2x+1
    private void createArray() {
        int X = transformToDetailed(width);
        int Y = transformToDetailed(height);

        array = new int[X][Y];

        /*
        for (int x = 1; x < array.length; x += 2) {
            for (int y = 1; y < array[0].length; y += 2) {
                array[x][y] = 1;
            }
        }
        */
        // TODO add loop for filling in middle-ones!!!
    }

    // TODO fix so it works for 2x+1
    // DONE (i think)
    private void addRail(int x, int y, String rail) {
        // array[x][y] = 1; old implementation, removed
        x = transformToDetailed(x);
        y = transformToDetailed(y);

        array[x][y] = 1;
        if (rail.equals("HorizontalRail")) {
            array[x + 1][y]++;
            array[x - 1][y]++;
        } else if (rail.equals("VerticalRail")) {
            array[x][y + 1]++;
            array[x][y - 1]++;
        } else {
            for (int dir = 0; dir < 4; dir++) {
                if (rail.indexOf(DirectionArrays.dirNames[dir]) >= 0) {
                    array[x + DirectionArrays.xDirs[dir]]
                         [y + DirectionArrays.yDirs[dir]]+=5;
                }
            }
        }
    }
}
