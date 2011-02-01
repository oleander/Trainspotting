
/**
 * Methods not fitting anywhere else
 */
public class Tools {

    /**
     * like monoidical plus
     */
    public static Runnable plusActions(final Runnable r1, final Runnable r2) {
        return new Runnable() {

            public void run() {
                r1.run();
                r2.run();
            }
        };
    }
}
