
import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Train extends Thread implements Runnable {

    private RailMap railMap;
    private Map<Sensor, RunnableTrain> pendingActions;
    private int currentVelocity;
    private int maxVelocity;
    private int id;
    private boolean goingForwards;
    //private int x0, y0;
    private Point point0;

    public Train(RailMap railMap, int maxVelocity, int id) {
        this.railMap = railMap;
        this.maxVelocity = maxVelocity;
        this.id = id;
        this.point0 = railMap.trainStartPos(id);

        this.pendingActions = new HashMap<Sensor, RunnableTrain>();
        goingForwards = true;
        setMaxVelocity();
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
            pendingActions.put(s, Tools.plusActions(prevAction, action));
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

            final int x = event.getXpos();
            final int y = event.getYpos();
            final Point point = new Point(x, y);
            Sensor sensor =
                    railMap.getSensorArray()[x][y];
            if (event.getStatus() == SensorEvent.INACTIVE) {
                if (pendingActions.containsKey(sensor)) {
                    pendingActions.get(sensor).run(this);
                    pendingActions.remove(sensor);
                }
            } else {
                int direction =
                        railMap.getDirectionTrainCameWith(point0, point);
                System.err.println("Hitted with direction " + direction);
                sensor.getAction(direction).run(this);
                point0 = point;
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
            sleep(400);
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

    public void trainAcquireSemaphor(Semaphore s) {
        try {
            s.acquire();
            say("Aquired semaphore " + s);
        } catch (InterruptedException ex) {
            say("error when aquire semaphore " + ex.getMessage());
        }
    }

    public void waitIfTakenThenGo(Semaphore s) {
        stopTrain();
        trainAcquireSemaphor(s);
        setMaxVelocity();
    }

    void releaseSemaphor(Semaphore s) {
        s.release();
    }
}
