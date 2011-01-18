package TSim;

/** SensorEvent represents the event of a train passing over a sensor.
 *  Every time a train passes over a sensor first an ACTIVE event is 
 *  created followed by an INACTIVE event when the train leaves the 
 *  sensor.
 *
 */

public class SensorEvent implements TSimInformation
{
    
    public static final int ACTIVE   = 0x01;
    public static final int INACTIVE = 0x02;

    protected int trainId;
    protected int status;
    protected int xPos;
    protected int yPos;
   
    /** Creates a new SensorEvent for a specific train and sensor.
     *  The train is represented by the trainId and the sensor by
     *  its coordinates.
     *
     *  @param trainId   the id of the train passing the sensor.
     *  @param xPos      the x coordinate of the sensor.
     *  @param yPos      the y coordinate of the sensor.
     *  @param status    the status of the sensor; either ACTIVE or INACTIVE/
     *
     */
 
    public SensorEvent(int trainId, int xPos, int yPos, int status)
    {
	this.trainId = trainId;
	this.xPos = xPos;
	this.yPos = yPos;
	this.status = status;
    }

    /** Returns the x coordinate of the affected sensor.
     *
     *  @return the x coordinate of the affected sensor.
     *
     */

    public int getXpos() 
    {
	return xPos;
    }

    /** Returns theyx coordinate of the affected sensor.
     *
     *  @return the y coordinate of the affected sensor.
     *
     */
    
    public int getYpos() 
    {
	return yPos;
    }

    /** Returns the status of the sensor; either ACTIVE or INACTIVE.
     *
     *  @return the status of the sensor.
     *
     */

    public int getStatus()
    {
	return status;
    }

    /** Returns the id of the train affecting the sensor.
     *
     *  @return the id of the train affecting the sensor.
     *
     */

    public int getTrainId()
    {
	return trainId;
    }


    public String toString() {
	return "train " + trainId + ", sensor at ("+xPos+","+yPos+") "+ 
	    (status==ACTIVE ? "active" : "inactive");
    }  
}


