
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 *  @author arash
 */
public class Train extends Thread implements Runnable{

    private RailMap railMap;
    private Map<Sensor, RunnableTrain > pendingActions;
    private int currentVelocity;
    private int maxVelocity;
    private int id;
    

    public Train(RailMap railMap, int maxVelocity, int id) {

        this.railMap = railMap;
        this.pendingActions = new HashMap<Sensor, RunnableTrain>();
        this.currentVelocity = 0;
        this.maxVelocity = maxVelocity;
        this.id = id;


        // initialisera första blockaden
    }

    /**
     * Run an action next time coming to a sensor
     */
    public void addOneTimeAction(Sensor s, RunnableTrain action){
        throw new NotImplementedException();
    }

    @Override
    public void run(){
        throw new NotImplementedException();
        
        
    }

    public void setVelocity(int v){
        say("Setting velocity " + v);
        throw new NotImplementedException();
    }

    public int getLastSetVelocity() {
        return currentVelocity;
    }

    public void say(String msg){
        System.err.println("Train " + id + "says: " + msg);
    }

    public void stopTrain(){
        say("Stopping train ...");
        setVelocity(0);
    }
    
    public void stopWaitTurnAround(){

        stopTrain();
        try {
            sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
        }
        setMaxVelocity();
    }

    public boolean isMovingUpOrRight(){
        return true;
    }

    public void setMaxVelocity() {
        say("Setting max velocity ...");
        setVelocity(maxVelocity);
    }
}
