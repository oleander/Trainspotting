import java.util.concurrent.Semaphore;

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
                        t.releaseSemaphor(s);
                    }
                });
            }
        };
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
        SearchResult searchSensor = railMap.getNextSensor(position, dir0);
        final SearchResult searchSwitch = railMap.getNextSwitch(position, dir0);

        if(searchSensor == null || searchSwitch == null){
            return Tools.getEmptyAction();
        }
        if(searchSwitch.distance > searchSensor.distance){
            // This means that the current sensor isn't the one nearest
            // the segement-switch
            return Tools.getEmptyAction();
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
            Train REMOVETHISTRAINLATER = null;
            REMOVETHISTRAINLATER.waitIfTakenThenGo(newSemaphore);
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, searchSwitch.direction);
            // TODO -- Add switching action
            REMOVETHISTRAINLATER.addOneTimeAction(nextSensor, new RunnableTrain() {
                public void run(Train t) {
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


            Train REMOVETHISTRAINLATER = null;
            REMOVETHISTRAINLATER.waitIfTakenThenGo(newSemaphore);
            railMap.switchSoGivenDirWorks(switchPos, oldDirection, alterantiveDirection);
            // TODO -- Add switching action
            REMOVETHISTRAINLATER.addOneTimeAction(nextSensor, new RunnableTrain() {
                public void run(Train t) {
                    t.releaseSemaphor(oldSemaphore);
                }
            });
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
