
import TSim.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lab1 {

    public static void main(String[] a) {
        new Lab1();
    }

    public Lab1(){
        driveTrain(1);
            /*
        TSimInterface inter = TSimInterface.getInstance();


        try {
            inter.setSpeed(1, 10);
        } catch (CommandException e) {
            e.printStackTrace();    // or only e.getMessage() for the error
            System.exit(1); // arash rocks
        }
             * */
    }

    public void driveTrain(int id) {
        TSimInterface inter = TSimInterface.getInstance();
        
        inter.setDebug(true);

        SensorEvent se = null;
        try {
            inter.setSpeed(id, 10);
            se = inter.getSensor(id);
        } catch (CommandException ex) {
            Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("se = " + se.toString());
    }
}
