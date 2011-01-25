
import TSim.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Lab1 {

    public static void main(String[] a) {
        new Lab1();
    }

    public Lab1() {
        configInterface();
        startTrains();

    }

    private void startTrains() {
//        final RailMap rm = new RailMap(new File("build/classes/bana"));
        final RailMap rm = new RailMap(new File("bana"));
        rm.printAsciiMap();
        
        for(int tid = 1; tid <= rm.getNumTrains(); tid++) {
            Train t = new Train(rm, 20, tid);
            t.start();
        }

        new Thread(new Runnable() {

            public void run() {
                //startControlTrainForever(2);
            }
        }).start();
    }

    public void configInterface() {
        TSimInterface inter = TSimInterface.getInstance();

        inter.setDebug(true);
    }

    public void startControlTrainForever(int id) {
        try {
            TSimInterface inter = TSimInterface.getInstance();
            inter.setSpeed(id, 10);
            while (true) {
                SensorEvent se = null;
                try {
                    se = inter.getSensor(id);
                } catch (CommandException ex) {
                    Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (CommandException ex) {
            Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
