
import TSim.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lab1 {

    public static void main(String[] a) {
        new Lab1();
    }

    public Lab1() {
        new Thread(new Runnable() {
            public void run() {
                startControlTrainForever(1);
            }
        }).start();

        new Thread(new Runnable() {

            public void run() {
                startControlTrainForever(2);
            }
        }).start();

        configInterface();
    }

    public void configInterface() {
        TSimInterface inter = TSimInterface.getInstance();

        inter.setDebug(true);
    }

    public void startControlTrainForever(int id) {
        TSimInterface inter = TSimInterface.getInstance();

        while (true) {
            SensorEvent se = null;
            try {
                inter.setSpeed(id, 10);
                se = inter.getSensor(id);
            } catch (CommandException ex) {
                Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.err.println("spöket laban");
        }
    }
}
