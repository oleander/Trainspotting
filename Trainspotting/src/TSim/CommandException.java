package TSim;

/** Thrown when a command to TSim fails. This might happen if the 
 *  train affected by the command has collided or derailed or if 
 *  the data to the command was erroneous. 
 */

public class CommandException extends Exception
{
    /** Constructs an CommandException with the specified detailed error
     *  message.
     *
     *  @param s   the detail message.
     *
     */
    
    public CommandException(String s)
    {
	super(s);
    }
}
