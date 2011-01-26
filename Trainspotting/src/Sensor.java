
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sensor {

    private Point position;
    private RailMap railMap;

    public Sensor(Point position, RailMap railMap) {
        this.position = position;
        this.railMap = railMap;
    }

    //given direction return action
    public RunnableTrain getAction(int dir0) {
        RunnableTrain a1 = getTurnAroundAction(dir0);
        RunnableTrain a2 = getCrossingAction(dir0);
        RunnableTrain a3 = getSegementSemaphorAction(dir0);

        return Tools.plusActions(a1, Tools.plusActions(a2, a3));
    }

    private RunnableTrain getCrossingAction(int dir0) {
        final SearchResult nextCross = railMap.getNextCrossing(position, dir0);
        final SearchResult nextSensor = railMap.getNextSensor(position, dir0);

        if (nextCross == null || nextSensor == null) {
            return Tools.getEmptyAction();
        }

        if (nextCross.distance > nextSensor.distance) {
            return Tools.getEmptyAction();
        }
        
        return new RunnableTrain() {

            public void run(Train t) {
                final Semaphore s = GlobalSemaphores.findOrCreate(nextCross.pos);
                t.waitIfTakenThenGo(s);
                t.addOneTimeAction(railMap.getSensor(nextSensor.pos), new RunnableTrain() {
                    public void run(Train t) {
                        s.release();
                    }
                });
            }
        }; /*
                int[][] array = railMap.getMinusOneFilledArray();
                int x = position.x, y = position.y;
                int revDir0 = (dir0 + 2) % 4;
                //array[x + DirectionArrays.xDirs[revDir0]][y + DirectionArrays.yDirs[revDir0]] = 1000;

                ArrayList<Point> interestingPoints = new ArrayList<Point>();
                RunnableTrain actions = Tools.getEmptyAction();

                int dir = dir0;
                Sensor foundSensor = null;
                while (array[x][y] == -1) {
                final int xf = x;
                final int yf = y;
                if (railMap.isKorsning(x, y)) {
                interestingPoints.add(new Point(x, y));
                }
                array[x][y] = dir;
                x += DirectionArrays.xDirs[dir];
                y += DirectionArrays.yDirs[dir];
                foundSensor = railMap.findSensor(x, y);
                if (foundSensor != null) {
                break;
                }
                }
                final Sensor fSensor = foundSensor;
                for (final Point point : interestingPoints) {
                if (railMap.isKorsning(point.x, point.y)) {
                actions = Tools.plusActions(actions, new RunnableTrain() {

                public void run(Train t) {
                final Semaphore s = GlobalSemaphores.findOrCreate(new Point(point.x, point.y));
                t.waitIfTakenThenGo(s);
                t.addOneTimeAction(fSensor, new RunnableTrain() {

                public void run(Train t) {
                s.release();
                }
                });
                }
                });
                }
                }


                return actions;
                 *
                 */
    }

    private RunnableTrain getTurnAroundAction(int dir0) {
        final SearchResult nextSensor = railMap.getNextSensor(position, dir0);
        if(nextSensor == null){
            return new RunnableTrain() {

                public void run(Train t) {
                    t.stopWaitTurnAround();
                }
            };
        }
        else{
            return Tools.getEmptyAction();
        }
    }

    private RunnableTrain getSegementSemaphorAction(int dir0) {
        return Tools.getEmptyAction();
    }
}
