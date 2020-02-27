package uniandes.lym.robot.view;

 
import java.lang.reflect.InvocationTargetException;








import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import uniandes.lym.robot.control.*;
import uniandes.lym.robot.kernel.*;



/**
 * Manages the application's command console with:
 *   input TextArea:   text area foe  user input
 *   output TextArea: History of commands sent and app's reponses 
 *   Button  to send   
 *   Button to quit
 * 
 * GUI of the Interpreter
 * Closely inspired on: TextDemo.java, from the Java tutorial:
 * <a href='http://java.sun.com/docs/books/tutorial/uiswing/components/textfield.html'>java.sun.com</a>
 *
 * @author  1. Juan Pablo Morales
 *          S. Takahashi
 *          Leandro Franco. Adds  command possibility of navigating command history 
 *          S. Takahashi :  implements  Property change listeners instead of Observer (deprecat4ed)
 *          
 * */

@SuppressWarnings("serial")
public class Console extends JPanel implements PropertyChangeListener { 
				
  	
	
	
	//Vector of commands for string commadn history
	
  	static Vector <String> commands = new Vector<String> ();

  	//number of commands
  	static int contCom = 0;	
	
	/**
	 * Interpreter that receives user input
	 */
	static private Interpreter interprete;
	/**
	 * Text area for putputing  system responses
	 */
	static private JTextArea sistema =  new JTextArea(13,20);
 
	
	
	/**
	 * Textarea for user commands
	 */
	static private JTextArea orden = new JTextArea(10,20);
	
	/**
	* BButton to send
	*/
	static private JButton enviar = new JButton("RUN");
	
	/**
	* Button ti quit
	*/
	static private JButton salir = new JButton("EXIT");

	/** 
	* ScrollPanes for input and output
	*/ 
           
	static private JScrollPane scrollPaneSistema = new JScrollPane(sistema);
	static private JScrollPane scrollPaneOrden = new JScrollPane(orden);

	/**
	 * Runnable class to control execution flow when writing 
	 *  on the system panel	 */
	
	final OutputPrinter escribirEnSistema = new OutputPrinter();
	
	

	static private Board tablero;
	static private Console consola;
	static private JFrame frame;
	static private RobotWorld world;
	/**
	 * 
	 * @param mundo Robot world to be controlled by the console
	 */
	
	public Console(RobotWorld mundo) {
		
		world = mundo;
		interprete = new Interpreter (world);

		mundo.addObserver(this,"messages");
		
	    orden.addKeyListener(new KeyAdapter(this));
		
	    addActions();
		
		//El log no se debe poder modificar
		
	    sistema.setEditable(false);
		
		
		/*
		 * El GridBagLayout es otra forma de poner los componentes, mucho mas
		 * flexible, que permite decidir como son las posiciones, dimensiones y
		 * comportamientos de los objetos unos con otros
		 */ 

		
		GridBagLayout gridBag = new GridBagLayout();
        setLayout(gridBag);
        GridBagConstraints c = new GridBagConstraints();
		
        //Hace que el componente ocupe todo lo ancho
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
		gridBag.setConstraints(scrollPaneSistema,c);
        c.weightx =1.0;
        c.weighty = 1.0;
        add(scrollPaneSistema);
        c.fill = GridBagConstraints.HORIZONTAL;
        gridBag.setConstraints(scrollPaneOrden,c);
        add(scrollPaneOrden);
        add(enviar);
     	add(salir);
     	
     	
   }
	
public static void relaunch(int dim){
		
		//tablero.reset(dim);
		frame.pack();
		
	    //Center the window
		Dimension frameSize = frame.getSize();
		frame.setResizable(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.setVisible(true);
	
 }	 
	
	/**
	 * synchronically writes on system TextArea 
	 * @param msg   message to add to system TextArea
	 */
	
	private void printOutput(String msg) {
	
		escribirEnSistema.setMessage(msg);
		try {
			SwingUtilities.invokeAndWait(escribirEnSistema);
		} catch (InterruptedException e) {
					e.printStackTrace();
		} catch (InvocationTargetException e) {
					e.printStackTrace();
		}
	
	}
	
	/**
	 * Adds send and exit  button actions
	 */
	private void addActions() {
		salir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
			 });
			 
		enviar.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
 					
				final Thread doInterpretar = new Thread() {
					public void run() {
						String texto;
						String resp;
						orden.setEditable(false);
						enviar.setEnabled(false);
						printOutput("USER  INPUT: " + orden.getText()+"\n");
						texto=orden.getText();
						orden.setText("");
						commands.addElement(texto);
						contCom = commands.size();
						resp=interprete.process(texto);
					    orden.setEditable(true);
						orden.setEnabled(true);
						enviar.setEnabled(true);
						printOutput(resp);
					}
				};
				doInterpretar.start();
			}
		}); 
	}

	void orden_keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

	    //up
		if (keyCode==38){
			if (contCom>0){
				orden.setText( (commands.elementAt(contCom-1)).toString() );
				contCom=contCom-1;
			}
		}

		//down
		if (keyCode==40){
			if (contCom < commands.size()-1 ){
				orden.setText( (commands.elementAt(contCom+1)).toString() );
				contCom=contCom+1;
			}
		}

	}
	
	
	/**
	 * Crea un control de tipo Console con su correspondiente visualizacion
	 * Para ello debe crear un Frame, ubicarlo en la pantalla y hacerlo
	 * visible. Todo eso se hace aqui
	 * Recibe un argumento cuando se ejecuta, que corresponde a las dimensiones
	 * del mundo
	 * Es decir que para llamar la aplicacion se debe hacer
	 * <pre>java interfaz.Consola 8</pre>
	 * Reemplazando 8 por el numero de casillas del mundo
	 */

	public static void main(String args[]) {
		//Si no hay exactamente un argumento o no es un numero debe mandar un
		//mensaje

		int size=0;
		int iniX=1,iniY=1, iniGlobos=1, iniFichas=1;
	  
		
		try {
	    if (args.length == 0) {
				size=  8;
				iniX = 1;
				iniY = 1;
				iniGlobos = 100;
				iniFichas = 64;
	    }
	   else if (args.length != 5) {
			throw new Exception("Expecting five arguments");
	    }
	    else {
			size= Integer.parseInt(args[0]);
			iniX = Integer.parseInt(args[1]);
			iniY = Integer.parseInt(args[2]);
			iniGlobos = Integer.parseInt(args[3]);
			iniFichas = Integer.parseInt(args[4]);
			if(iniX<1 || iniX>size) throw new Exception("La posicion inicial delrobot en X debe estar dentro del mundo" );
			if(iniY<1 || iniY>size) throw new Exception("La posicion inicial delrobot en Y debe estar dentro del mundo" );
			if(iniGlobos < 0) throw new Exception("El numero de globos debe ser positivo" );
			if(iniY<1 || iniY>size) throw new Exception("El numero de fichas debe ser positivo" );
		} 
	    }
		catch(Exception ex) {
			System.out.println("Error: "+ex.getMessage()+"\nUso: java interfaz.Consola <numCasillas> <X inicial robot> <Y inicial Robot>");
			System.exit(1);
		}
	
		frame = new JFrame("Robot World");
		/* Hace que la aplicacion salga cuando se cierra esta ventana*/
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }  
        });
		 
		tablero = new Board(new RobotWorldDec(size, new Point(iniX,iniY),iniGlobos, iniFichas));
		
		
		consola = new Console(tablero.getMundo());
		
		//Hace que lo que hay en el archivo se pueda mover con barras
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(tablero);
		frame.getContentPane().add(consola,BorderLayout.SOUTH);
		
		//frame.getContentPane().add(debugPanel,BorderLayout.EAST);
		frame.pack();
		
	    //Center the window
		Dimension frameSize = frame.getSize();
		frame.setResizable(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.setVisible(true);
	 }

	
	/*
	 * @param evt : event fired by the robot world
	 * evt.getNewValue()  returns a string that should be displayed on  the system panel
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */

	 
	@Override
	 public void propertyChange(PropertyChangeEvent evt) {
		   Object arg = evt.getNewValue() ;
		   if (arg instanceof String ){
			   String msg =  (String) arg ;
			   printOutput("----> "+msg);
				 
			 }
			
	    }
	
/**
 * 
 * @author Silvia Takahashi
 *
 * Clase para poder ejecutar sincronicamente los cambios en el 
 * Panel de sistema
 * 
 */
class OutputPrinter implements Runnable {

	String message;
	
	public OutputPrinter() {
	}
	public void run() {
				sistema.append(message);		
	}
	public void setMessage(String s) {
	 	message = s;
	 }
}

class KeyAdapter extends java.awt.event.KeyAdapter {
  
  Console adaptee;

  KeyAdapter(Console adaptee) {
    this.adaptee = adaptee;
  } 
  
  public void keyPressed(KeyEvent e) {
    adaptee.orden_keyPressed(e);
  }
 
}


}
