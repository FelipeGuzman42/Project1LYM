package uniandes.lym.robot.control;



import uniandes.lym.robot.kernel.*;
import uniandes.lym.robot.control.*;




/**
 * Receives commands and relays them to the Robot. 
 */

public class Interpreter   {
	
	/**
	 * Robot's world
	 */
	  private  RobotWorldDec rworld;   
	 /* The Parser */
	  private RobotParser parser;
	  
	
	public RobotWorld  getWorld( ) {
		return rworld;
	}
	
	//public Interpreter()
	  //{
	  //}


    /**
	 * Creates a new interpreter for a given world
	 * @param world  robot world where instructions will operate
	 */


	public Interpreter(RobotWorld world)
      {
		this.rworld =  (RobotWorldDec) world;
		parser = new RobotParser(System.in);
	  	parser.setWorld(world);
	  }
	
	
	/**
	 * sets the world
	 * @param world  new robot world
	 */

	public void setWorld(RobotWorld world) 
	{
		world = (RobotWorldDec) world;
		System.out.println("set "+world);
		
	}
	  
	
	
	/**
	 *  Processes a sequence of commands. A command is a letter  followed by a ";"
	 *  The command can be:
	 *  
	 *  
	 * @param input string with a  sequence of commands
	 * @return interpreter  response
	 */
	
	public String process(String input) throws Error
     {   
		 //  Sends input to the parser
		
		parser.ReInit(new java.io.StringReader(input));
		
		StringBuffer output=new StringBuffer("System: \n-->");	
		
	 
			
		try {                  		
			if (parser.instructions(output)){
				
				output.append(" \n--> ");
			}
		}  
         catch(ParseException pex)
         {		
         		output.append("Syntax Error:"+pex.getMessage()+" \n--> ");
         		
    			
         }
		 catch (Error err)
		 {
			 output.append("Error: "+err.getMessage()+" \n--> ");
				
			 
			 
		 }
		 catch (Exception error)
		 {
			 output.append("Robot Run Time exception"+error.getStackTrace()+" \n--> ");
				
		 }
		  return output+" End Input \n";
	  }
	
}
	    