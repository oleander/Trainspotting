
public class Sensor {
    private Point position;

    //given direction return action
    public RunnableTrain getAction(int direction) {
        return new RunnableTrain() {

            public void run(Train t) {
                t.stopWaitTurnAround();
            }
        };
        /*
         * We write lambda functions this way
        return new RunnableTrain() {
            public void run(Train t) {
                
            }
        };
         *
         */
    }

    public Sensor(Point position) {
        this.position = position;
    }

}
