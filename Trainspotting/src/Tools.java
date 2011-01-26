/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arash
 */
public class Tools {
    public static RunnableTrain getEmptyAction(){
        return new RunnableTrain() {
            public void run(Train t) {
                
            }
        };
    }

    public static RunnableTrain plusActions(final RunnableTrain t1, final RunnableTrain t2){
        return new RunnableTrain() {
            public void run(Train t) {
                t1.run(t);
                t2.run(t);
            }
        };
    }
}
