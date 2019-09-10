import java.util.NoSuchElementException;

public class Rod implements Comparable<Rod> {

	private int numberOfDisks; // tracks the number of disks on this rod
	private Disk[] disks; // stores references to the disks on this rod
							// index 0: bottom, index discNumber-1: top

	/**
	 * Constructs a new rod that can hold a maximum of maxHeight Disks. The
	 * numberOfDisks new Disks will be created with sizes between 1 and
	 * numberOfDisks (inclusive), and arranged from largest (on bottom) to the
	 * smallest (on top) on this Rod.
	 * 
	 * @param maxHeight
	 *            is the capacity or max number of Disks a rod can hold.
	 * @param numberOfDiscs
	 *            is the initial number of Disks created on this rod.
	 */
	public Rod(int maxHeight, int numberOfDisks) {
		disks = new Disk[maxHeight];
		for (int i = numberOfDisks; i > 0; i--) {
			push(new Disk(i));
		}
	}

	/**
	 * Adds one new Disk to the top of this rod.
	 * 
	 * @param disk
	 *            is a reference to the Disk being added to this rod.
	 * @throws IllegalStateException
	 *             when this rod is already full to capacity.
	 */
	public void push(Disk disk) throws IllegalStateException {
		if (numberOfDisks == disks.length)
			throw new IllegalStateException();
		disks[numberOfDisks] = disk;
		numberOfDisks++;
	}

	/**
	 * Removes and returns one Disk from the top of this rod.
	 * 
	 * @return a reference to the Disk that is being removed.
	 * @throws NoSuchElementException
	 *             when this rod is empty.
	 */
	public Disk pop() throws NoSuchElementException {
		if (isEmpty())
			throw new NoSuchElementException();
		Disk diskPop = disks[numberOfDisks - 1];
		disks[numberOfDisks - 1] = disks[numberOfDisks - 2];
		return diskPop;
	}

	/**
	 * Returns (without removing) one Disk from the top of this rod.
	 * 
	 * @return a reference to the Disk that is being returned.
	 * @throws NoSuchElementException
	 *             when this rod is empty.
	 */
	public Disk peek() throws NoSuchElementException {
		if (isEmpty())
			throw new NoSuchElementException();
		return disks[numberOfDisks - 1];
	}

	/**
	 * Indicates whether this rod is currently holding zero Disks.
	 * 
	 * @return true when there are no Disks on this rod.
	 */
	public boolean isEmpty() {
		return disks[0] == null;
	}

	/**
	 * Indicates whether this rod is currently full to its capacity with disks.
	 * 
	 * @return true when the number of Disks on this rod equals its max height.
	 */
	public boolean isFull() {
		return disks[numberOfDisks] != null;
	}

	/**
	 * Compares one rod to another to determine whether it's legal to move the
	 * top disk from this rod onto the other.
	 * 
	 * @param other
	 *            is the destination rod we are considering moving a disk to.
	 * @return +1 when moving a disk from this rod to other is legal, -1 when
	 *         moving a disk from this rod to other is illegal, or 0 when this
	 *         rod is empty and there are no disks to move.
	 */
	@Override
	public int compareTo(Rod other) {
		if (other == null)
			return 1;
		if (this.isEmpty())
			return 0;
		if (disks[this.numberOfDisks - 1].compareTo(other.disks[other.numberOfDisks - 1]) == 1)
			return -1;
		return 1;

	}

	/**
	 * The string representation of this rod includes its max height number of
	 * rows separated by and ending with newline characters (\n). Rows occupied
	 * by a disk will include that disk's string representation, and other rows
	 * instead contain a single vertical bar character (|). All rows are
	 * centered by surrounding both sides with spaces until they are each
	 * capacity*2+1 characters wide. Example of 5 capacity rod w\3 disks: " |
	 * \n" + " | \n" + " <=2=> \n" + " <==3==> \n" + "<====5====>\n"
	 * 
	 * @return the string representation of this rod based on its contents.
	 */
	@Override
	public String toString() {
		String rod = "";
		int lineLength = disks.length * 2 + 1;
		String blankLine = "|";
		for (int i = 0; i < lineLength/2; i++)
		blankLine = " " + blankLine + " ";
	blankLine += "\n";
	for(int i = numberOfDisks; i < disks.length; i++)
		rod += blankLine;
		for (int i = numberOfDisks - 1; i >= 0; i--){
			String disk = disks[i].toString();
			int diskLength = disk.length();
			if (diskLength < lineLength) {
				int numSpaces = (lineLength - diskLength) / 2;
				for (int j = 0; j < numSpaces; j++)
					disk = " " + disk + " ";
			}
			rod += disk + "\n";
		}
		return rod;
}
}
