package TSim;

/** CommandStatus represents the responses from TSim that does not
 *  carry information about sensors or trains but rather if
 *  the command issued succeeded or not. 
 *
 */

public class CommandStatus implements TSimInformation
{
    public static final int OK               = 0x01;
    public static final int ILLEGAL_SPEED    = 0x02;
    public static final int NO_SUCH_TRAIN    = 0x03;
    public static final int NO_SUCH_SWITCH   = 0x04;
    public static final int NO_SUCH_POS      = 0x05;
    public static final int TRAIN_ON_SWITCH  = 0x06;


    protected int status;

    /** Creates a new CommandStatus message.
     *
     *  @param status   the status of the command.
     *
     */ 
    
    public CommandStatus(int status) 
    {
	this.status = status;
    }
    
    /** Returns the status of the command.
     *
     * @return the status of the command.
     *
     */

    public int getStatus()
    {
	return status;
    }

    /** Returns the string representation of the CommandStatus class.
     *  this representation is used when creating exception messages.
     *
     *  @return the string representation of the stauts of the command.
     *
     */

    public String toString()
    {
	String res = "unknown status";

	switch (status) {
	case OK :
	    res = "ok";
	    break;
	case ILLEGAL_SPEED :
	    res = "illegal speed";
	    break;
	case NO_SUCH_TRAIN :
	    res = "no such train";
	    break;
	case NO_SUCH_SWITCH :
	    res = "no such switch";
	    break;
	case NO_SUCH_POS :
	    res = "no such position";
	    break;
	case TRAIN_ON_SWITCH :
	    res = "train on switch";
	    break;
	}
	
	return res;
    }

}
