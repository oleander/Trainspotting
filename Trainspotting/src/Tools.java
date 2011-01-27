/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arash
 */
public class Tools {

    public static Runnable plusActions(final Runnable r1, final Runnable r2){
        return new Runnable() {
            public void run() {
                r1.run();
                r2.run();
            }
        };
    }
}
