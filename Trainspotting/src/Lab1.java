
import TSim.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Lab1 {

    public static void main(String[] a) {
        new Lab1();
//        RailMap rm = new RailMap();
//        System.out.println("hallås");
//        rm.parse(new File("build/classes/bana"));
    }

    public Lab1() {
        configInterface();
        startTrains();

    }

    private void startTrains() {
        new Thread(new Runnable() {

            public void run() {
                startControlTrainForever(1);
            }
        }).start();
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
