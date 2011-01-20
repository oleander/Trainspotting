
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arash
 */
public class Train extends Thread implements Runnable{

    private RailMap railMap;
    private Map<Sensor, ArrayList<String> > pendingActions;
    private int velocity;
    private int id;

    public Train(RailMap railMap, int velocity, int id) {
        this.railMap = railMap;
        this.pendingActions = new HashMap<Sensor, ArrayList<String>>();
        this.velocity = velocity;
        this.id = id;


        // initialisera första blockaden
    }
    

    @Override
    public void run(){
        // ...
        
        
    }

}
