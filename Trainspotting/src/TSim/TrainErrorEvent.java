package TSim;

/** Class representing an (erroneous) event for a train. 
 *
 */

public class TrainErrorEvent implements TSimInformation 
{

    public static final int TRAIN_COLLISION = 0x01;
    public static final int STOP_COLLISION  = 0x02;
    public static final int DERAILMENT      = 0x03;
    
    protected int event;
    protected int trainId;
   
    /** Creates a new TrainErrorEvent for the specified train id and with 
     *  the specified event type.
     *
     *  @param trainId   the id of the train affected by the event.
     *  @param event     the type of the event.
     *
     */
 
    public TrainErrorEvent(int trainId, int event)
    {
	this.trainId = trainId;
	this.event = event;
    }
    
    /** Returns the type of the event.
     *
     *  @return the type of the event.
     *
     */

    public int getEvent()
    {
	return event;
    }

    /** Returns the id of the train affected byt the event.
     *
     *  @return the id of the train effected by the event.
     *
     */

    public int getTrainId() 
    {
	return trainId;
    }

    /** Returns a string representation of the event. This is used when 
     *  creating exception messages.
     *
     *  @return the string representation of the event.
     *
     */
    
    public String toString() 
    {
	String eventString = "unknown event";
	
	switch (event) {
	case TRAIN_COLLISION :
	    eventString = " train collision";
	    break;
	case STOP_COLLISION :
	    eventString = " stop collision";
	    break;
	case DERAILMENT :
	    eventString = " derailment";
	    break;
	}
	
	return "Fatal error for train " + String.valueOf(trainId) + " :" +
	    eventString;
    }
}







