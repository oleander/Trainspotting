import java.util.concurrent.Semaphore;

public class Sensor {

    private Point position;
    private RailMap railMap;

    public Sensor(Point position, RailMap railMap) {
        this.position = position;
        this.railMap = railMap;
    }

    //given direction return action
    public void getAction(int dir0, final Train t) {
        getTurnAroundAction(dir0, t);
        getCrossingAction(dir0, t);
         getSegementSemaphorAction(dir0, t);
    }

    private void getCrossingAction(int dir0, final Train t) {
        final SearchResult nextCross = railMap.getNextCrossing(position, dir0);
        final SearchResult nextSensor = railMap.getNextSensor(position, dir0);

        if (nextCross == null || nextSensor == null) {
            return ;
        }

        if (nextCross.distance > nextSensor.distance) {
            return ;
        }
        
        final Semaphore s = GlobalSemaphores.findOrCreate(nextCross.pos);
        t.waitIfTakenThenGo(s);
        t.addOneTimeAction(railMap.getSensor(nextSensor.pos), new Runnable() {
            public void run() {
                t.releaseSemaphor(s);
            }
        });
        
    }

    private void getTurnAroundAction(int dir0, Train t) {
        final SearchResult nextSensor = railMap.getNextSensor(position, dir0);
        if(nextSensor == null){
            t.stopWaitTurnAround();
        }
    }

    private void getSegementSemaphorAction(int dir0, final Train t) {
        SearchResult searchSensor = railMap.getNextSensor(position, dir0);
        final SearchResult searchSwitch = railMap.getNextSwitch(position, dir0);

        if(searchSensor == null || searchSwitch == null){
            return ;
        }
        if(searchSwitch.distance > searchSensor.distance){
            // This means that the current sensor isn't the one nearest
            // the segement-switch
            return ;
        }

        Sensor nextSensor = railMap.getSensor(searchSensor.pos);
        final Semaphore oldSemaphore = railMap.getSegmentSemaphor(position);
        Semaphore newSemaphore = 
                railMap.getSegmentSemaphor(nextSensor.position);


        Point switchPos = searchSwitch.pos;
        int oldDirection = searchSwitch.direction;
        final int alterantiveDirection = railMap.otherSwitchDirection(switchPos, oldDirection);
        
        boolean couldAquire = newSemaphore.tryAcquire();
        if(couldAquire || alterantiveDirection == -1){
            // if alternativeDirection == -1, then we have no choice
            
            t.waitIfTakenThenGo(newSemaphore);
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, searchSwitch.direction);
            // TODO -- Add switching action
            t.addOneTimeAction(nextSensor, new Runnable() {
                public void run() {
                    t.releaseSemaphor(oldSemaphore);
                }
            });
        }else{
            // Ok, we simply must search again, but taking the other direction
            // of the switch, we know that there is another direction since
            // alternativeDirection >= 0            
            searchSensor = railMap.getNextSensor(switchPos, alterantiveDirection);
            nextSensor = railMap.getSensor(searchSensor.pos);
            newSemaphore = railMap.getSegmentSemaphor(nextSensor.position);


            t.waitIfTakenThenGo(newSemaphore);
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, alterantiveDirection);
            // TODO -- Add switching action
            t.addOneTimeAction(nextSensor, new Runnable() {
                public void run() {
                    t.releaseSemaphor(oldSemaphore);
                }
            });
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
