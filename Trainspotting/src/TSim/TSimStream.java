package TSim;

import java.io.*;

/** Reads text from the given input stream connected to TSim and
 *  returns a stream of TSimInformation.
 *
 **/

public class TSimStream {
    protected StreamTokenizer sTokenizer;
    
    /** Creates a new TSimStream that reads from the given InputStream.
     * 
     *  @param in the input stream to read text from.
     *
     */

    public TSimStream(InputStream in) {
	BufferedReader inReader = 
	    new BufferedReader( new InputStreamReader(in));

	sTokenizer = new StreamTokenizer(inReader);
    }
   
    /** Reads from the input stream and returns an object representing
     *  the message from TSim.
     *
     *  @return next message from TSim
     *  @throws UnparsableInputException when the input from TSim is 
     *                                   not parsable.
     **/
 
    public TSimInformation read() throws UnparsableInputException {
	String cmd = readString(sTokenizer);
	
	TSimInformation info = null;

	if (cmd.equals("SuccessStatus"))
	    info = new CommandStatus(CommandStatus.OK);
	else if (cmd.equals("NoSwitchStatus"))
	    info = new CommandStatus(CommandStatus.NO_SUCH_SWITCH);
	else if (cmd.equals("IllegalTrainNoStatus"))
	    info = new CommandStatus(CommandStatus.NO_SUCH_TRAIN);
	else if (cmd.equals("IllegalReverseStatus"))
	    info = new CommandStatus(CommandStatus.ILLEGAL_SPEED);
	else if (cmd.equals("IllegalRailPosStatus"))
	    info = new CommandStatus(CommandStatus.NO_SUCH_POS);
	else if (cmd.equals("IllegalSwitchingStatus"))
	    info = new CommandStatus(CommandStatus.TRAIN_ON_SWITCH);
	else if (cmd.equals("Sensor")) {
	    
	    int id   = readInt(sTokenizer);
	    int xPos = readInt(sTokenizer);
	    int yPos = readInt(sTokenizer);
	    
	    int sStatus = SensorEvent.ACTIVE;
	    
	    if (readString(sTokenizer).equals("inactive"))
		sStatus = SensorEvent.INACTIVE;
	    
	    info = new SensorEvent(id, xPos, yPos, sStatus);
	    
	}
	else if (cmd.equals("TrainCollisionEvent")) {
	    
	    int id = readInt(sTokenizer);
	    
	    info = new TrainEvent(id, TrainEvent.TRAIN_COLLISION);
	}
	else if (cmd.equals("StopCollisionEvent")) {

	    int id = readInt(sTokenizer);
	    
	    info = new TrainEvent(id, TrainEvent.STOP_COLLISION);
	}
	else if (cmd.equals("DerailmentEvent")) {
	    int id = readInt(sTokenizer);
	    
	    info = new TrainEvent(id, TrainEvent.DERAILMENT);
	}
	else throw new UnparsableInputException
	    ("unknown information from tsim");
	
	/* What if there is something that would result in a TT_EOL.
	   Shouldn't we handle this here?
	*/

	return info;
	
    }
    
    /** Help method that reads a string from the tokenizer and
     *  returns it or throws an exception if the next token was not 
     *  a string.  Upon reading EOF the whole system exits.
     *
     *  @param sTokenizer the stream tokenizer to read from.
     *  @return the read string.
     *  @throws UnparsableInputException when the next token was not 
     *                                   a string.
     */


    protected String readString(StreamTokenizer sTokenizer) 
	throws UnparsableInputException {
	try {
	    int type = sTokenizer.nextToken();

	    if (type == StreamTokenizer.TT_EOF)
		System.exit(1);
	    
	    if (type != StreamTokenizer.TT_WORD)
		throw new UnparsableInputException
		    ("string expected");
	    
	    return sTokenizer.sval;
	}
	catch (IOException e) {
	    throw new UnparsableInputException(e.getMessage());
	}
	
    }

    /** Help method that reads an integer from the tokenizer and
     *  returns it or throws an exception if the next token was not 
     *  an integer.  Upon reading EOF the whole system exits.
     *
     *  @param sTokenizer the stream tokenizer to read from.
     *  @return the read integer.
     *  @throws UnparsableInputException when the next token was not 
     *                                   an integer.
     */

    protected int readInt(StreamTokenizer sTokenizer) 
	throws UnparsableInputException {
	try {
	    
	    int type = sTokenizer.nextToken();

	    if (type == StreamTokenizer.TT_EOF)
		System.exit(1);
	    
	    if (type != StreamTokenizer.TT_NUMBER)
		throw new UnparsableInputException
		    ("int expected");
	    
	    return (int) sTokenizer.nval;
	}
	catch (IOException e) {
	    throw new UnparsableInputException(e.getMessage());
	}
    }
    
}
