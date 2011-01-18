package TSim;

import java.util.concurrent.*;


/** The TSimInterface is the intended interface between TSim and the 
 *  laboration. It handles the extraction of information from TSim and
 *  provides methods for manipulating trains, sensors and switches.
 *
 */

public final class TSimInterface {

    public static final int SWITCH_LEFT  = 0x01;
    public static final int SWITCH_RIGHT = 0x02;

    private static TSimInterface tsim;
    
    /* We maintain one buffer (commandFIFO) of command replies from tsim
     * and one buffer of sensor events for each train (sensorVec(trainId)).

     * Further we maintain a vector of TrainErrorEvents indexed by trainId,
     * where a non-null entry means a fatal event has occurred.
     */

   private LinkedBlockingQueue<CommandStatus> commandFIFO     
	= new LinkedBlockingQueue<CommandStatus>();

    private AddingArrayList<LinkedBlockingQueue<SensorEvent>> sensorVec = 
	new AddingArrayList<LinkedBlockingQueue<SensorEvent>>();
    
    private AddingArrayList<TrainErrorEvent> trainVec = 
	new AddingArrayList<TrainErrorEvent>();

    private TSimStream sTSim;
    private boolean debug = false;


    /** Create a new TSimInterface
     *
     */

    private TSimInterface() {
	sTSim = new TSimStream(System.in);
    }
    
   /** Returns the single instance (singleton pattern).
     *  In first call, creates an instance of this class
     *  and starts a thread executing its run() method.
     *
     */
    public static synchronized TSimInterface getInstance() {
	if (tsim == null) {
	    tsim = new TSimInterface();
	    new Thread(new Runnable() {
		    public void run() {
			tsim.readLoop();
		    }
		}).start();
	}
	
	return tsim;
    }
    
    /** Repeatedly reads messages from tsim; runs in a separate thread.
     */

    private void readLoop() 
    {
	while (true) {
	    
	    try {
		TSimInformation dInfo = sTSim.read();

		if (debug) System.err.println("     " + dInfo);
		
		if (dInfo instanceof CommandStatus) 
		    commandFIFO.offer((CommandStatus)dInfo);
		
		else if (dInfo instanceof TrainErrorEvent) {
		    TrainErrorEvent tEvent = (TrainErrorEvent) dInfo;
		    int trainId = tEvent.getTrainId();
		    /* Store the error event to make future commands 
		       concerning this train result in an exception.
		    */
		    
		    trainVec.set(trainId, tEvent);
		    reportTrainErrorEvent(tEvent);
		}

		else if (dInfo instanceof SensorEvent) {
		    SensorEvent sEvent = (SensorEvent) dInfo;
		    int trainId = sEvent.getTrainId();
		    
		    getSensorEventQueue(trainId).put(sEvent);
		}
	    }
	    catch (UnparsableInputException e) {
		System.err.println(e.getMessage());
	    }
	    catch (InterruptedException e) {
		System.err.println(e.getMessage());
	    }
	}
    }

    private void reportTrainErrorEvent(TrainErrorEvent e) {
	System.err.println(e);
    }

 
    private LinkedBlockingQueue<SensorEvent>  getSensorEventQueue(int trainId) {
	LinkedBlockingQueue<SensorEvent> trainSensorFIFO = 
	    sensorVec.get(trainId);
	if (trainSensorFIFO==null) {
	    trainSensorFIFO = new LinkedBlockingQueue<SensorEvent>();
	    sensorVec.set(trainId,trainSensorFIFO);
	}			
		    
	return trainSensorFIFO;
    }

    /** Turns on and off printing of debug info to System.err.
     */

    public void setDebug(boolean debug) {this.debug = debug;}



    /** Sets the speed of a train.
     *
     *  @param trainId  the id of the train to be affected by the command.
     *  @param speed    the new speed of the train.
     *  @throws CommandException  if the supplied id was false (NO_SUCH_TRAIN),
     *                            if the speed was illegal (ILLEGAL_SPEED)
     *                            or if the train had crashed.
     *
     */

    public synchronized void setSpeed(int trainId, int speed)
	throws CommandException {
	TrainErrorEvent tEvent = trainVec.get(trainId);
	
	/* some event has happened for this train i.e. it has crashed */
	
	if (tEvent != null)
	    throw new CommandException(tEvent.toString());
        String output ="SetSpeed " + trainId + " " + speed;
	System.out.println(output);
	if (debug) System.err.print(output);

	try {
	    CommandStatus cStat = commandFIFO.take();
	    if (cStat.getStatus() != CommandStatus.OK)
		throw new CommandException(cStat.toString());
	} catch (InterruptedException e) {}

    }

    /** Sets the direction of the specified switch. Valid directions are
     *  SWITCH_LEFT and SWITCH_RIGHT.
     * 
     *  @param xPos   the x coordinate of the switch.
     *  @param yPos   the y coordinate of the switch.
     *  @param switchDir  the new direction of the switch.
     *
     *  @throws CommandException if the coordinates of the switch were invalid
     *                          (NO_SUCH_SWITCH) or if there was a train on
     *                          the switch (TRAIN_ON_SWITCH)
     *
     */
     

    public synchronized void setSwitch(int xPos, int yPos, int switchDir)
	throws CommandException {
	
	String output  ="SetSwitch " + xPos + " " + yPos + 
	                (switchDir == SWITCH_LEFT ? " LeftSwitch" : " RightSwitch");
	System.out.println(output);
	if (debug) System.err.print(output);
	
	try {
	    CommandStatus cStat = commandFIFO.take();
	    if (cStat.getStatus() != CommandStatus.OK)
		throw new CommandException(cStat.toString());
	} catch (InterruptedException e) {}
	
    }

    
    /** Blocks the calling thread until the specified train passes a sensor.
     *
     *  @param trainId  the id of the train to wait for.
     *  @return a SensorEvent representing the information about the event
     *  @throws  CommandException if the train has crashed.
     *
     */


    public SensorEvent getSensor(int trainId) 
	throws CommandException, InterruptedException {
	TrainErrorEvent tEvent = trainVec.get(trainId);
	
	/* some event has happened for this train i.e. it has crashed */
	
	if (tEvent != null)
	    throw new CommandException(tEvent.toString());

	return getSensorEventQueue(trainId).take();
    }

}











