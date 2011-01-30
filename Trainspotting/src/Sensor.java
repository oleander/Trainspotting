
import java.util.concurrent.Semaphore;

/**
 * One Sensor isntance for each actual sensor, the sensors don't
 * contain important fields. Only method is getAction().
 * For most purposes in it's calculations it's just uses the RailMap.
 */
public class Sensor {

    private Point position;
    private RailMap railMap;

    public Sensor(Point position, RailMap railMap) {
        this.position = position;
        this.railMap = railMap;
    }

    /**
     * When hitting this sensor, trains should call this with the direction
     * they came with, then a suitable action will be taken.
     *
     * @param dir0 the direction the train comes with
     * @param t    the train that should perform the actions
     */
    public void getAction(int dir0, final Train t) {
        getTurnAroundAction(dir0, t);
        getCrossingAction(dir0, t);
        getSegementSemaphorAction(dir0, t);
    }


    private void getTurnAroundAction(int dir0, Train t) {
        final SearchResult nextSensor = railMap.getNextSensor(position, dir0);
        if (nextSensor == null) {
            t.stopWaitTurnAround();
        }
    }

    private void getCrossingAction(int dir0, final Train t) {
        final SearchResult nextCross = railMap.getNextCrossing(position, dir0);
        final SearchResult nextSensor = railMap.getNextSensor(position, dir0);

        if (nextCross == null || nextSensor == null) {
            return;
        }

        if (nextCross.distance > nextSensor.distance) {
            return;
        }

        final Semaphore s = GlobalSemaphores.findOrCreate(nextCross.pos);
        t.waitIfTakenThenGo(s);
        t.addOneTimeAction(railMap.getSensor(nextSensor.pos), new Runnable() {

            public void run() {
                t.releaseSemaphor(s);
            }
        });

    }

    private void getSegementSemaphorAction(int dir0, final Train t) {
        SearchResult searchSensor = railMap.getNextSensor(position, dir0);
        final SearchResult searchSwitch = railMap.getNextSwitch(position, dir0);

        if (searchSensor == null || searchSwitch == null) {
            return;
        }
        if (searchSwitch.distance > searchSensor.distance) {
            // This means that the current sensor isn't the one nearest
            // the segement-switch
            return;
        }

        Sensor nextSensor = railMap.getSensor(searchSensor.pos);
        final Semaphore oldSemaphore = railMap.getSegmentSemaphor(position);
        Semaphore newSemaphore =
                railMap.getSegmentSemaphor(nextSensor.position);


        Point switchPos = searchSwitch.pos;
        int oldDirection = searchSwitch.direction;
        final int alterantiveDirection = railMap.otherSwitchDirection(switchPos, oldDirection);

        boolean couldAquire = newSemaphore.tryAcquire();
        if (couldAquire) {
            // newDirection should preferably be to move "forward" (=oldDirection)
            int newDirection =
                    railMap.canMoveInDirection(switchPos, oldDirection) ? oldDirection : alterantiveDirection;

//            System.err.println("alternative 1");
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, newDirection);
            t.addOneTimeAction(nextSensor, new Runnable() {

                public void run() {
                    t.releaseSemaphor(oldSemaphore);
                }
            });
        } else if (alterantiveDirection >= 0) {
            // Ok, we simply must search again, but taking the other direction
            // of the switch, we know that there is another direction since
            // alternativeDirection >= 0
//            System.err.println("alternative 2");
            searchSensor = railMap.getNextSensor(switchPos, alterantiveDirection);
            nextSensor = railMap.getSensor(searchSensor.pos);
            newSemaphore = railMap.getSegmentSemaphor(nextSensor.position);


            t.waitIfTakenThenGo(newSemaphore);
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, alterantiveDirection);
            t.addOneTimeAction(nextSensor, new Runnable() {

                public void run() {
                    t.releaseSemaphor(oldSemaphore);
                }
            });
        } else {
            // since alternativeDirection == -1, then we must wait for other...

//            System.err.println("alternative 3");
            t.waitIfTakenThenGo(newSemaphore);
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, oldDirection);
            t.addOneTimeAction(nextSensor, new Runnable() {

                public void run() {
                    t.releaseSemaphor(oldSemaphore);
                }
            });

        }

    }
}

