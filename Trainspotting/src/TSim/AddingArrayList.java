package TSim;

import java.util.ArrayList;

/** Maintains an ArrayList of elements of type E, with size increased as needed.
Difference to ArrayList's 
 *  methods are <br>
 *  - ArrayIndexOutOfBoundException is not thrown when index >= size; instead 
 *    size is increased and array filled with null's.<br>
 *  - Methods are synchronized.
 */



public class AddingArrayList<E> {

    private ArrayList<E> vec;

    /** Creates an ArrayList using default constructor.
     */

    public AddingArrayList() {
	vec = new ArrayList<E>();
    }

    private void setSize(int newSize) {
	for(int i = vec.size(); i<newSize; i++) vec.add(null);
    }

    /** Assigns elem at position index.
     *  if needed, size is expanded to be at least index+1.
     */

    public synchronized void set(int index, E elem) {
	if (index < 0) throw new ArrayIndexOutOfBoundsException("Negative Index");
	if (index >= vec.size()) setSize(index+1);
	vec.set(index,elem);
    }

    /** Returns element at position index.
     *  If necessary, array is first expanded to size at least index+1.
     */

    public synchronized E get(int index) {
	if (index < 0) throw new ArrayIndexOutOfBoundsException("Negative Index");
	if (index >= vec.size()) setSize(index+1);
	return vec.get(index);
    }
}

    
