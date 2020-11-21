import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A server to handle sketches: getting requests from the clients,
 * updating the overall state, and passing them on to the clients
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 */

/**
 * May 27th 2019
 * PS-6
 * 
 * @author Scott Crawshaw
 * @author Kunaal Verma
 */

public class SketchServer {
	private ServerSocket listen;						// for accepting connections
	private ArrayList<SketchServerCommunicator> comms;	// all the connections with clients
	private Sketch sketch;								// the state of the world
	private int id = 0;
	
	public SketchServer(ServerSocket listen) {
		this.listen = listen;
		sketch = new Sketch("");
		comms = new ArrayList<SketchServerCommunicator>();
	}

	public Sketch getSketch() {
		return sketch;
	}
	
	/**
	 * The usual loop of accepting connections and firing off new threads to handle them
	 */
	public void getConnections() throws IOException {
		System.out.println("server ready for connections");
		while (true) {
			SketchServerCommunicator comm = new SketchServerCommunicator(listen.accept(), this);
			comm.setDaemon(true);
			comm.start();
			addCommunicator(comm);
		}
	}

	/**
	 * Adds the communicator to the list of current communicators
	 */
	public synchronized void addCommunicator(SketchServerCommunicator comm) {
		comms.add(comm);
	}

	/**
	 * Removes the communicator from the list of current communicators
	 */
	public synchronized void removeCommunicator(SketchServerCommunicator comm) {
		comms.remove(comm);
	}

	/**
	 * Sends the message from the one communicator to all (including the originator)
	 */
	public synchronized void broadcast(String msg) {
		for (SketchServerCommunicator comm : comms) {
			comm.send(msg);
		}
	}
	
	public synchronized void handleMessage(String msg) {
        if (!msg.equals("")){
        	//need server to create new shape id
            if(msg.startsWith("DRAW")) {
            	msg = msg.substring(0, msg.indexOf(",") + 1) + id + "," + msg.substring(msg.indexOf(",") + 1);
            	id++;
            }
            //message format: TYPE,message
			String[] messageArr = msg.split(",");
			String type = messageArr[0];
			
			if(type.equals("DRAW")) {
				sketch.addShape(msg.substring(msg.indexOf(",") + 1));
			}
			if(type.equals("MOVE")) {
				sketch.moveShape(msg.substring(msg.indexOf(",") + 1));
			}
			if(type.equals("RECOLOR")) {
				sketch.recolorShape(msg.substring(msg.indexOf(",") + 1));
			}
			if(type.equals("DELETE")) {
				sketch.removeShape(msg.substring(msg.indexOf(",") + 1));
			}
			
			broadcast(msg);
		}
        
	}
	
	public static void main(String[] args) throws Exception {
		new SketchServer(new ServerSocket(4242)).getConnections();
	}
}
