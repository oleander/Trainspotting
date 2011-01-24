
import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Train extends Thread implements Runnable {

    private RailMap railMap;
    private Map<Sensor, RunnableTrain> pendingActions;
    private int currentVelocity;
    private int maxVelocity;
    private int id;
    private boolean goingForwards;
    private int x0, y0;

    public Train(RailMap railMap, int maxVelocity, int id) {
        this.railMap = railMap;
        this.maxVelocity = maxVelocity;
        this.id = id;
        this.x0 = railMap.trainStartPos(id).x;
        this.y0 = railMap.trainStartPos(id).y;

        this.pendingActions = new HashMap<Sensor, RunnableTrain>();
        goingForwards = true;
        // initialisera första blockaden

    }

    /**
     * Run an action next time coming to a sensor (or leaving the sensor?)
     */
    // TODO: decide wheater it is for entering or leaving sensor
    public void addOneTimeAction(Sensor s, final RunnableTrain action) {
        if (pendingActions.containsKey(s)) {
            // already exists one, must concatenate existing with new action.
            final RunnableTrain prevAction = pendingActions.get(s);
            pendingActions.put(s, new RunnableTrain() {

                public void run(Train t) {
                    prevAction.run(t);
                    action.run(t);
                }
            });
        } else {
            pendingActions.put(s, action);
        }
    }

    @Override
    public void run() {
        TSimInterface iface = TSimInterface.getInstance();

        while (true) {
            SensorEvent event = null;
            try {
                event = iface.getSensor(id);
            } catch (CommandException ex) {
                System.err.println("interface didn't allow getting sensor!!!");
            } catch (InterruptedException ex) {
                System.err.println("train got interrupted waiting for sensor!");
            }

            Sensor sensor =
                    railMap.getSensorArray()[event.getXpos()][event.getYpos()];
            if (event.getStatus() == SensorEvent.INACTIVE) {
                continue; // TODO: for now completely ignoring inactive events.
            } else {
                if (pendingActions.containsKey(sensor)) {
                    pendingActions.get(sensor).run(this);
                    pendingActions.remove(sensor);
                }
                int direction =
                        getDirectionTrainCameWith(x0, y0, event.getXpos(), event.getYpos());
                sensor.getAction(direction).run(this);
            }
        }


    }

    public void setVelocity(int velocity) {
        say("Setting velocity " + velocity);
        TSimInterface iface = TSimInterface.getInstance();
        try {
            iface.setSpeed(id, velocity);
        } catch (CommandException ex) {
            System.err.println("error setting velocity!!!!!!!!!");
        }
        currentVelocity = velocity;
    }

    public int getLastSetVelocity() {
        return currentVelocity;
    }

    public void say(String msg) {
        System.err.println("Train " + id + " says: " + msg);
    }

    public void stopTrain() {
        say("Stopping train ...");
        setVelocity(0);
    }

    public void stopWaitTurnAround() {

        stopTrain();
        goingForwards ^= true; // turn direction
        try {
            sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
        }
        setMaxVelocity();
    }

    public boolean isMovingUpOrRight() {
        return true;
    }

    public void setMaxVelocity() {
        say("Setting max velocity ...");
        setVelocity((goingForwards ? 1 : -1) * maxVelocity);
    }

    private int getDirectionTrainCameWith(int x0, int y0, int x1, int y1){
        Queue<Point> queue = new LinkedList<Point>();
        final Point startPoint = new Point(x0, y0);
        queue.add(startPoint);

        int[][] arr = railMap.getMinusOneFilledArray();

        while(!queue.isEmpty()){
            Point p = queue.poll();
            if(arr[p.x][p.y] != -1){
                continue;
            }
            arr[p.x][p.y] = 1000; //mark visited
            for (int dir = 0; dir < 4; dir++) {
                int x = p.x + DirectionArrays.xDirs[dir];
                int y = p.y + DirectionArrays.yDirs[dir];
                
                if(x < 0 || x >= arr.length || y < 0 || y >= arr[0].length){
                    continue; // so we don't search outside the bounds
                }

                if(x == x1 && y == y1){
                    return dir;
                }
                queue.add(new Point(x, y));
            }
        }

        System.err.println("bfs failed!");
        return -123;
    }
}
