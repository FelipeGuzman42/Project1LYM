package uniandes.lym.robot.kernel;



import java.awt.Point;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;




/**
 * Robot World is an square board with a robot that moves from cell to cell 
 * There are chips and balloons. There can only be one chip per cell. There may be more than one
 * balloon per cell. The robot starts with some chips and some balloons.
 * The Robot can put and pickUp chips and balloons. When the robot places a chip, 
 * it falls  to the bottom row, or to the row above the last placed chip. 
 * Balloons float and there can be more than one balloon in a cell.
 * When a balloon is placed, it stays in the cell where it was placed.
 * <b>Note :</b>Invalid operations cause unexpected behavior, no exceptions are raised.
 * 
 * This class does not  implement visualization.
 * A RobotWorld communicates  with the interface using PropertyChangeSupport. This class used to extend Observable 
 *  
 *  Cells are identified by a pair: (x,y): x is the position on the x axis, y the position on the y axis.  
 *  By row and columns : x is the column identifier; y is the  row identifier
 *  
 *  If the world were 4 by 4,  this is the distribution of the cells:
 *  
 *  (1,1)  (2,1)  (3,1) (4,1)
 *  (1,2)  (2,2)  (3,2) (4,2)
 *  (1,3)  (2,3)  (3,3) (4,3)
 *  (1,4)  (2,4)  (3,4) (4,4)
 *  
 *  
 *  
 */



public class RobotWorld 
{

	
	
	
	
/** 
 * Constants to model the direction that the robot is facing: This is called the robot's orientation
 * 	
 */
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	/**
	 * The dimension of the board. Being a square, only one value suffices.
	*/
	private int size;
	
	/**  Cell where the robot is located */
	private Point position;
	
	/**
	 * Set of positions that contain chips
	 */
	
	private HashSet <Point> chipContainer;
	
	/**  Number of balloons held by the Robot*/
	
	private int myBalloons;
	
	/** Number of Chips held by the Robot*/
	
	private int myChips;
	
	/**  Array used to store the number of balloons in each cell */
	
	private int balloonContainer[][];
	
	/**  Direction (North, South, East or West) the robot is facing */
	
	private int orientation;
	
	private HashMap<String, Integer> hm;
	
	/**
	 * RobotWorld constructor
	 * @param dim  World dimension
	 * @param pos Robot's Initial position
	 * @param initB initial number of balloons
	 * @param initC initial number of chips
	 */
	
	public RobotWorld(int dim, Point pos, int initB, int initC) {
		
		
		this.size = dim;
		this.position = pos;
		this.myChips = initC;
		this.myBalloons = initB;
		
		
		this.chipContainer = new HashSet <Point>();
		this.balloonContainer = new int[dim][dim];

		this.change = new Change();
		
		
		this.orientation = NORTH;
		
		hm = new HashMap<>();
		
	}

	
	/** 
	 * Default constructor:  size 8, at position (1,1) with 640 balloons and 64 chips 
	 */
	public RobotWorld(){
		this(8,new Point(1,1),640,64);
	}	

	//
	//  methods for reintializing
	//
	
	public HashMap<String, Integer> darHM()
	{
		return hm;
	}
	


	/**
	 * Reinitializes world attributes
	 * @param pos Robot's  position
	 * @param newB new number of balloons
	 * @param newC new number of chips
	 * @param clean:  if false then then the current balloons and chips are left on the board
	 *                 if true the cells are left without chips and without balloons.
	 */
	
	public void reinit(Point pos, int newB, int newC, boolean clean) {
		int i,j;
		
		position = pos;
		myBalloons = newB;
		myChips = newC;
		if (clean) {
			for(i=0; i<size; i++) {
				for (j=0; j<size; j++)
				{
					balloonContainer[i][j]=0;
				}
			}
			chipContainer.clear();			
		}

		change.setEnd(new Point(size+1,size+1));
		change.setStart(new Point(size+1,size+1));
		//this.setChanged();
		//this.notifyObservers(this.cambio);
		pcs.firePropertyChange("world", size, change);
}
	
	
	/**
	 * Reinitializes world attributes
	 * @param dim  new size
	 * @param pos Robot's  position
	 * @param newB new number of balloons
	 * @param newC new number of chips
	 * @param clean:  if false then then the current balloons and chips are left on the board
	 *                 if true the cells are left without chips and without balloons.
	 */
	
	public void reinit(int dim, Point pos, int newB, int newC, boolean clean) {

		int i,j;
		int tempGlobos[][] = new int[dim][dim];
		int  minDim = (size<dim)?size: dim; 
		Point tempFichas[] = (Point [])chipContainer.toArray();
		
		size = dim;
		position = pos;
		myBalloons = newB;
		myChips = newC;
		
		
		if (clean) {
			chipContainer.clear();		
			balloonContainer = tempGlobos;
		}
		else {	
			for(i=0; i<minDim; i++) {
				for (j=0; j<minDim; j++)
				{
					tempGlobos[i][j]=balloonContainer[i][j];
				}
			}
			balloonContainer = tempGlobos;			
			if (dim < size) {		
				for (i=0; i < tempFichas.length; i++) {
					Point pf1 = tempFichas[i];
					if (pf1.x > dim || pf1.y > dim) {
						chipContainer.remove(pf1);
					}
				}
			}
		}
		
//		El cambio es "nada"
		this.change = new Change();
		change.setEnd(new Point(size+1,size+1));
		change.setStart(new Point(size+1,size+1));
		//this.setChanged();
				//this.notifyObservers(this.cambio);
				pcs.firePropertyChange("world", size, change);
}

	
	
	/** 
	 * Modifies the world dimension without changing anything else. the 
	 * size is decreased and the robots eds up outside the new dimensions, it is placed at the end of the world
	 * @param dim new size
	 */
	
	public void reinit(int dim) {

		int i,j;
		int tempGlobos[][] = new int[dim][dim];
		int  minDim = Math.min(dim,size); 
		Point tempFichas[] = (Point [])chipContainer.toArray();
				
		
		
	     position.x = Math.min(dim,position.x);
		 position.y =  Math.min(dim,position.y);
				
		for(i=0; i<minDim; i++) {
			for (j=0; j<minDim; j++) {
				tempGlobos[i][j]=balloonContainer[i][j];
			}
		}	
		
		if (dim < size) {		
			for (i=0; i < tempFichas.length; i++) {
				Point pf1 = tempFichas[i];
				if (pf1.x > dim || pf1.y > dim) {
					chipContainer.remove(pf1);
				}
			}
		}
		
		balloonContainer = tempGlobos;			
		size = dim;
		
		this.change = new Change();
		change.setEnd(new Point(size+1,size+1));
		
				pcs.firePropertyChange("world", size, change);
		
	}	
	
	
	
	
	
	/**
	 * Turns the robot 90 degrees to the right. 
	 */
	public void turnRight(){
		if(this.orientation == NORTH){
			this.orientation = EAST;
		}else if(this.orientation == SOUTH){
			this.orientation = WEST;
		}else if(this.orientation == EAST){
			this.orientation = SOUTH;
		}else {
			this.orientation = NORTH;
		}
		informar();
        informar("turnRight ");
	}
	
	
	/**
	 * Move the robot west. 
	 * If the robot cannot move west, the behavior is not defined
	 */
	
	public void moveWest() {
		
		this.change.setStart(new Point(this.position));
		this.position.translate(-1,0);
		findErrors();
		informar();
		informar("moveWest ");

	}

	/**
	 * Move the robot east.  Move 1 along the x axis.
	 * If the robot cannot move east, the behavior is not defined
	 */
	public void moveEast() {
		this.change.setStart(new Point(this.position));
		this.position.translate(1,0);
		findErrors();
		informar();
		informar("moveEast ");
	}

	/**
	 * Move the robot north: move the robot -1 along the y axis
	 * If the robot is at the top, the behavior is not defined
	 */

	public void moveNorth() {
		this.change.setStart(new Point(this.position));
		this.position.translate(0,-1);
		findErrors();
		informar();
		informar("moveNorth ");
	}
	/**
	 * Move the robot south: move the robot 1 along the y axis
	 * If the robot is at the  bottom, the behavior is not defined
	 */
	public void moveSouth() {
		this.change.setStart(new Point(this.position));
		this.position.translate(0,1);
		findErrors();
		informar();
		informar("moveSouth ");
		
	}
	
	/**
	 *  Puts a balloon on the robot's current position, incrementing the number of balloons in that position.
	 *  If the robot has no balloons, the behavior is not defined
	 */
	
	public void putBalloon() {
		int x = (int)this.position.getX();
		int y = (int)this.position.getY();	
		this.balloonContainer[x-1][y-1]++;
		this.myBalloons--;
		findErrors();
		informar();
		informar("PutBalloon ");
	}

	/**
	 * Picks a balloon from the Robot's position
	 * If there are no balloons, the behavior is not defined.
	 */
	public void pickBalloon() {
		int x = (int)this.position.getX();
		int y = (int)this.position.getY();	
		this.balloonContainer[x-1][y-1]--;
		this.myBalloons++;
		findErrors();
		informar();
		informar("GrabBalloon ");
	}
	
	
	/**
	 * Robot puts  a chip. The chip falls to the first cell in the column that does not have a chip.
	 * If there is already a chip on the Robot's cell, then errorBehavior is invoked.
	 * 
	 * */
	public void putChip() {
		
		int y = position.y;
		int x = position.x;
		int b;

		Point p= new Point(this.position);
		
		
		if (this.anyChips(p)) {
			errorBehavior("Can't put chip");
			informar();
		}
		else {	
			
			for(b=this.size;b >= y;b--) {
				p.setLocation(x,b);
				if (!this.anyChips(p)) {
					this.change.setStart(new Point(p));				
					this.chipContainer.add(p);
					myChips--;
					findErrors();
					informar("PutChip ");
					informar();
					break;
				}
			}	
		}
	}
	/**
	 * Attempts to pickup a chip.
	 * If there is a chip, then the robot keeps it and all chips above it fall one position
	 * If there is no chip, the behavior is undefined.
	 */
		
	public void pickChip() {
		int y = (int)this.position.getY();
		int x = (int)this.position.getX();
		int b;
		
		
		Point anterior,siguiente;
		
		anterior = 	new Point(this.position);
		
		siguiente = new Point(this.position);
			
		if(!this.anyChips(this.position)) {
			errorBehavior("No chip to pick");
			informar();
		}
		else {
	    // hay dos casos: 
		//    1. Hay fichas encima
		//    2. No hay fichas encima
		
		  myChips++;

		  if (y != 1) {
			siguiente.setLocation(siguiente.getX(),y-1);
			if (! this.anyChips(siguiente)){
			
				this.change.setStart(this.position);
				this.chipContainer.remove(this.position);
				
				informar();
			}
            else {
              if (y-2 < 0) {
				  anterior.setLocation(siguiente.getX(),siguiente.getY());
			  }
			  else {                    
				for(b=y-2;b >= 0 && this.anyChips(siguiente);b--){
					anterior.setLocation(siguiente.getX(),siguiente.getY());
					siguiente.setLocation(siguiente.getX(),b);
			   	}
              }
			  this.change.setStart(anterior);
			  this.chipContainer.remove(anterior);
			  informar();
              this.position.setLocation(x,y);  
			  informar();
			}		
		  }
          else {
			this.change.setStart(this.position);
			this.chipContainer.remove(this.position);
			informar();
			}

			informar("GetChip ");

		}
	}
	
	//
	// Private methods
	//
	
	/**
	 * Used to determine if there was an error:
	 *  Robot's position outside the board
	 *  Robot with a negative number of chips or balloons
	 *  Positions with a negative number of balloons
	 *  Errors due to placing chips are taken care of it the method putChips
	 *  If there is an error, errorBehavior is invoked 
	 *  */
	
	private void findErrors() {
		int	x = (int)this.position.getX();
		int y = (int)this.position.getY();		
		if(x < 1 || x > this.size) {
				errorBehavior("Illegal column: "+ x);
		}
		else if ( y<1 || y>this.size )  {
				errorBehavior("Illegal row: "+ y);
		}
		else if (this.myChips < 0) {
			errorBehavior("No chips to put:");
		}
		else if ( this.myBalloons < 0 ) {
			errorBehavior("No ballons to put");
		}
		else if (this.balloonContainer[x-1][y-1]< 0){
			errorBehavior("No nballons to pick");
		}
	}

	/**
	* @param message  error message
	* Error Behavior: world without chips, balloons, and the robot at a random postion
	*/
	private void errorBehavior(String message) throws Error  {
		
		
		
		Random rand = new Random();

		// Obtain a number between [1 - size].
		int x = rand.nextInt(size)+1;
		int y = rand.nextInt(size)+1;
		
		
		this.position = new Point(x,y);			
		this.myChips = 0;
		this.myBalloons = 0;
		
		change.setEnd(new Point(size+1,size+1));
		change.setStart(new Point(0,0));
		
		informar(" !!!! Runtime  ERROR: "+ message +  "  !!!!\n");
		pcs.firePropertyChange("world", size, change);
		
		throw new Error(message);
	}


	
	
	//
	// Metodos analizadores
	//
	/**
	 * @return robot's current posiiotn
	 */
	public Point getPosition() {
		return this.position;
	}
	/**
	 * @return board size
	 */
	public int getN() {
		return this.size;
	}
	/**
	* @return number of balloons held by the robot
    */
	public int getMyBalloons() {
		return this.myBalloons;
	}
	/**
	* @return nuimber of chips held by the robot
    */
	public int getMyChips() {
			return this.myChips;
	}

	/**
	 * @param p    coordinates of a cell on the board
	 * @return true if there is a chip in cell p; false otherwise 
	 * 
	 * */
	public boolean anyChips(Point p) {
		return this.chipContainer.contains(p);
	}
	/**
	 * @return true if there is a chip in the robot's current position; false otherwise 
	 * 
	 * */
	public boolean anyChips() {
		return this.chipContainer.contains(this.position);
	}
	/**
	 * @param p cell on the board
	 * @return number of balloons on p
	 */
	public int countBalloons(Point p) {
		return this.balloonContainer[p.x-1][p.y-1];
	}
	
	/**
	 * @return number of balloons on the robot's position
	 */
	public int countBalloons() {
		return this.balloonContainer[(this.position).x-1][(this.position).y-1];
	}
	
	/**
	 * @return  true if there's at least one balloon in the robot's cell
	 */
	public boolean anyBalloons() {
		if ( this.balloonContainer[(this.position).x-1][(this.position).y-1] > 0 )
		{
			return true;
		}
		return false;
	}
	/**
	 * @return true if the robot is at the top of the board; false otherwise
	 * 
	 * */
	public boolean atTop() {
		return this.position.y==1;
	}
	
	/**
	 * @return true if the robot is at the bottom of the board; false otherwise
	 * 
	 * */
	public boolean atBottom() {
		return this.position.y==this.size;
	}
	
	/**
	 * @return true if the robot on the last cell to left; false otherwise
	 * 
	 * */
	
	public boolean atLeft() {
		return this.position.x==1;
	}
	/**
	 * @return true if the robot on the first cell to the right; false otherwise
	 * 
	 * */
	public boolean atRight() {
		return this.position.x==this.size;
	}

 
	 

	
	/**
	 * verifies whether or not  robot is facing north
	 * @return true if robot is facing north; false otherwise
	 */
	public boolean facingNorth() {
		return this.orientation==NORTH;
	}
	/**
	 * verifies whether or not  robotis facing south
	 * @return true if robot is facing south; false otherwise
	 */
	public boolean facingSouth() {
		return this.orientation==SOUTH;
	}
	/**
	 * verifies whether or not  robot is facing east
	 * @return true if robot is facing east; false otherwise
	 */
	public boolean facingEast() {
		return this.orientation==EAST;
	}
	/**
	 * verifies whether or not  robot is facing west
	 * @return true if robot is facing west; false otherwise
	 */
	public boolean facingWest() {
		return this.orientation==WEST;
	}
	
	/**
	 * 
	 * @return The robot's orientation SOUTH, EAST OR WEST).
	 */
	public int getOrientation(){
		return this.orientation;
	}
	
/* ---------------- */
	
	
	//   Attributes and methods for  simulating deprecated Observable Interface

	/**   Last change performed on the world */
	
	private Change change;
	
	
	
	
	/**
	 * Used to communicate  changes to the interface.  We use change, to avoid repainting the whole board.
	 * 
	 */
	
	
	protected void informar() {
	
		this.change.setEnd(this.position);
		pcs.firePropertyChange("world", size, change);
	
	}
	

	/**
	 * Used to communicate  changes to the interface`s  system pane.  
	 * @param   message  :
	 * the robot's position is also printed
	 * 
	 */
	protected void informar(String message) {
		int x = (int)this.position.getX();
		int y = (int)this.position.getY();	
		
		pcs.firePropertyChange("messages", size, message+": ("+x+","+y+")\n");
	
	}
	
	PropertyChangeSupport pcs = new  PropertyChangeSupport(this);
	
	
	public void addObserver(PropertyChangeListener l, String property) {
		pcs.addPropertyChangeListener(property, l);
	}

	
	
}
