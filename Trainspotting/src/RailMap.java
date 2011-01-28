
import TSim.CommandException;
import TSim.TSimInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class RailMap {

    private int width, height;
    private int[][] array; //true if walkable
    private Sensor[][] sensorArray;
    private ArrayList<Point> trainList;

    RailMap(File file) {
        trainList = new ArrayList<Point>();
        parse(file);
    }

    public Sensor getSensor(Point p) {
        return sensorArray[p.x][p.y];
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
        array = new int[transformToDetailed(width)][transformToDetailed(height)];
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
                boolean isSensor = sline[sline.length - 1].equals("Sensor");

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

    // doesn't print sensors
    public void printAsciiMap() {
        System.err.println("");
        for (int y = 0; y < array[0].length; y++) {
            for (int x = 0; x < array.length; x++) {
                if (array[x][y] > 0) {
                    System.err.print(array[x][y]);
                } else {
                    System.err.print("#");
                }
            }
            System.err.println("");
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.err.print(getNumAdjacentDirections(new Point(x, y)));
            } 
            System.err.println("");
        }
    }

    public boolean isCrossing(Point p) {
        return getNumAdjacentDirections(p) == 4;
    }

    public boolean isSwitch(Point p) {
        return getNumAdjacentDirections(p) == 3;
    }

    public boolean isEnd(Point p) {
        return getNumAdjacentDirections(p) == 1;
    }

    private int getNumAdjacentDirections(Point p) {
        int numAdjacent = 0;
        for (int dir = 0; dir < 4; dir++) {
            numAdjacent += canMoveInDirection(p, dir) ? 1 : 0;
        }

        return numAdjacent;
    }

    public boolean canMoveInDirection(Point from, int dir) {
        for (int k = 1; k <= 2; k++) {
            int x = transformToDetailed(from.x) + DirectionArrays.xDirs[dir] * k;
            int y = transformToDetailed(from.y) + DirectionArrays.yDirs[dir] * k;
            if (!existingDetailedCoordinate(x, y)) {
                return false;
            }
        }
        return true;
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

    SearchResult getNextSensor(final Point from, int dir0) {
        return searchForPredicate(from, dir0, new PointCond() {

            public boolean ok(Point p) {
                return !from.equals(p) && getSensor(p) != null;
            }
        });
    }

    private SearchResult searchForPredicate(Point from, int dir, PointCond pc) {
        Point now = new Point(from.x, from.y);
        int dist = 0;
        while (!pc.ok(now)) {
            dir = getPrefferedDirection(now, dir);
            //System.err.println(now);
            //System.err.println(dir);
            if (dir == -1) {
                return null;
            }
            now.moveInDirection(dir);
            dist++;
        }
        return new SearchResult(now, dir, dist);
    }

    private int getPrefferedDirection(Point p, int dir) {
        int x = transformToDetailed(p.x);
        int y = transformToDetailed(p.y);
        int[] preferredDirs = {dir, (dir + 1) % 4, (dir - 1 + 4) % 4};

        int alternativeSwitchDir = otherSwitchDirection(p, dir);
        if (alternativeSwitchDir >= 0) {
            preferredDirs[1] = alternativeSwitchDir;
        }
        // following if statement is for "t-korsning", it should take which is
        // rail-ically able
//        if(array[x+DirectionArrays.xDirs[preferredDirs[1]]]
//                [y+DirectionArrays.yDirs[preferredDirs[1]]] <
//           array[x+DirectionArrays.xDirs[preferredDirs[2]]]
//                [y+DirectionArrays.yDirs[preferredDirs[2]]]){
//            int temp = preferredDirs[1];
//            preferredDirs[1] = preferredDirs[2];
//            preferredDirs[2] = temp;
//        }
        for (int d : preferredDirs) {
            if (canMoveInDirection(p, d)) {
                return d;
            }
        }
        return -1;
    }

    private boolean validDetailedCoordinate(int x, int y) {
        return !(x <= 0 || y <= 0 || x >= transformToDetailed(width) || y >= transformToDetailed(height));
    }

    private boolean existingDetailedCoordinate(int x, int y) {
        return validDetailedCoordinate(x, y) && array[x][y] > 0;
    }

    Semaphore getSegmentSemaphor(Point position) {
        if (getNumAdjacentDirections(position) > 2) {
            System.err.println("position = " + position);
            throw new AssertionError();
        }
        // we can give senseless directions because it will prioritize different
        // directions when searching, and we assume this is only called
        // on straight
        SearchResult s1 = getNextSwitchOrEnd(position, 0);
        SearchResult s2 = getNextSwitchOrEnd(position, 2);
//        System.err.println("s1 = " + s1);
//        System.err.println("s2 = " + s2);
        Point p1 = s1.pos;
        Point p2 = s2.pos;

        // This is so we can see distance between semaphores that start and
        // stop at exact same points, yet are different tracks
        // (like orig bana in middle)
        p1.x += s1.direction * 1000;
        p2.x += s2.direction * 1000;

        return GlobalSemaphores.findOrCreate2(p1, p2);
    }

    /**
     * Get `the other` direction train can go with at a switch
     * Will fail if not a switch or cant take other dir
     * at switch
     *
     * @param switchPos    the position of the switch
     * @param oldDirection the direction the train comes with
     * @return -1 if fails, otherwise the direction train can go
     */
    int otherSwitchDirection(Point switchPos, int oldDirection) {
        int revDir = (oldDirection + 2) % 4;
        int xCameFrom = transformToDetailed(switchPos.x) + DirectionArrays.xDirs[revDir];
        int yCameFrom = transformToDetailed(switchPos.y) + DirectionArrays.yDirs[revDir];
        if (array[xCameFrom][yCameFrom] < 5) {
            return -1;
        }

        for (int unModdedDir = oldDirection + 1; unModdedDir < oldDirection + 1 + 4; unModdedDir += 2) {
            final int x = transformToDetailed(switchPos.x);
            final int y = transformToDetailed(switchPos.y);
            int dir = unModdedDir % 4;
            if (array[x + DirectionArrays.xDirs[dir]][y + DirectionArrays.yDirs[dir]] >= 5) {
                return dir;
            }
        }
        return -1;
    }

    void switchSoGivenDirWorks(Point switchPos, int dirTrainComesFrom, int dirTrainWantsToGo) {
//        System.err.println(switchPos);
//        System.err.println(dirTrainComesFrom);
//        System.err.println(dirTrainWantsToGo);
        TSimInterface iface = TSimInterface.getInstance();
        int x = transformToDetailed(switchPos.x);
        int y = transformToDetailed(switchPos.y);
        boolean b = dirTrainComesFrom != dirTrainWantsToGo;
        b ^= array[x + 1][y] >= 5;
        b ^= array[x][y + 1] >= 5;
        b ^= array[x][y - 1] > 0 && array[x][y + 1] > 0;
        try {
            iface.setSwitch(switchPos.x, switchPos.y, b ? TSimInterface.SWITCH_LEFT : TSimInterface.SWITCH_RIGHT);
        } catch (CommandException ex) {
            System.err.println("switch failade");
        }
    }

    private interface PointCond {

        public boolean ok(Point p);
    }

    // TODO: I'm quite sure this isn't enough!!!
    // TODO: THIS IMPLEMENTATION ISN*T ENOUGH (but enough for origbana)
    public int getDirectionTrainCameWith(Point p0, Point p1) {
        System.err.println("bfsing from " + p0 + " to " + p1);
        Queue<Point> queue = new LinkedList<Point>();
        Set<Point> visitedPoints = new HashSet<Point>();
        queue.add(p0);

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (visitedPoints.contains(p)) {
                continue;
            }
            visitedPoints.add(p);//mark visited
            for (int dir = 0; dir < 4; dir++) {
                Point movedPoint = Point.createNewAndMove(p, dir);
                if (movedPoint.equals(p1)) {
                    System.err.println("dir = " + dir);
                    int retDir = getPrefferedDirection(movedPoint, dir);
                    System.err.println("retDir = " + retDir);
                    return retDir;
                }

                if (!canMoveInDirection(p, dir)) {
                    continue;
                }

                queue.add(movedPoint);
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
                    array[x + DirectionArrays.xDirs[dir]][y + DirectionArrays.yDirs[dir]] += 5;
                }
            }
        }
    }
}
