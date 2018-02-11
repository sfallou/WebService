import java.util.*;
import org.apache.xmlrpc.*;

// javac Message.java

// javac -cp "org.apache.commons.codec_1.6.0.v201305230611.jar;xmlrpc-2.0.jar;." ChatRoom.java
// java -cp "org.apache.commons.codec_1.6.0.v201305230611.jar;xmlrpc-2.0.jar;." ChatRoom

// jar -cvfm ChatRoom.jar manifestChatRoom ChatRoom.class Message.class org.apache.commons.codec_1.6.0.v201305230611.jar xmlrpc-2.0.jar
// java -jar ChatRoom.jar

public class ChatRoom extends Thread {

	private int numMsg = 0;
	private HashMap<String, String> users;
	private HashMap<Integer, Message> messages;
	private String room = "";
	private int port = 80;

	public ChatRoom(String room, int port){
		this.room = room;
		this.port = port;
		users = new HashMap<>();
		messages = new HashMap<>();
	}

	public void run() {
		try { 
			System.out.println("Attempting to create a discussion room based on an XML-RPC Server...");
			// if (!isDigit(port) || (isDigit(port) && (port < 0 || port > 65535))) {
			//	port = 80;
			// }
			
			WebServer server = new WebServer(port);
			server.addHandler("server", this);
			server.start();
			System.out.println("The discussion room " + room + " has been created successfully!");
			System.out.println("Server ready to accept requests on port " + port + "...");
		}
		catch (Exception exception){
			System.err.println("ChatRoom run" + room + " : " + exception);
		}
	}

	public Boolean subscribe(String pseudo) {
		Boolean result = false;
		try {
			 Message msg = new Message(0, pseudo, "I just joined the room discussion " + room + ".", room);
			 postMessage(msg.getPoster(), msg.getContent(), msg.getRoom());
			 users.put(pseudo, pseudo);
			 result = true;
		}
		catch (Exception exception){
			System.err.println("Failed to register to the ChatRoom " + room + " : " + exception);
		}
		return result;
	}

	public Boolean unsubscribe(String pseudo){
		Boolean result = false;
		try {
			 Message msg = new Message(0, pseudo, "I just left the room discussion " + room + ".", room);
			 postMessage(msg.getPoster(), msg.getContent(), msg.getRoom());
			 users.remove(pseudo);
			 result = true;
		}
		catch (Exception exception){
			System.err.println("ChatRoom unsubscribe. Failed to leave the ChatRoom " + room + " : " + exception);
		}
		return result;
	}

	public String getMessages(int numLastMsg){
		String result = "";
		for (Message msg : messages.values()) {
			if (msg.getId() > numLastMsg) {
				result = result + msg.getId() + ";" + msg.getPoster() + ";" + msg.getContent() + ";" + msg.getRoom() + ":";
			}
		}
		return result;
	}

	public Boolean postMessage(String poster, String content, String room){
		Boolean result = false;
		try {
			numMsg++;
			Message msg = new Message(numMsg, poster, content, room);
			messages.put(msg.getId(), msg);
			System.out.println(msg.getRoom() + " [" + msg.getId() + "]	" + msg.getPoster() + ": " + msg.getContent());
			result = true;
		}
		catch (Exception exception){
			System.err.println("ChatRoom postMessage " + room + " : " + exception.toString());
			exception.printStackTrace();
		}
		return result;
	}

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		System.out.println("You are going to create a new discussion room (SS server).");
		System.out.println("Please reconfigure the proxy by editing it's config file to make this discussion room visible by users!");
		System.out.print("Give the discussion room's name : ");
		String room = sc.nextLine();
		System.out.print("Give the listenning port of this server (E.g: 80): ");
		int port = 80;
		try {
			port = sc.nextInt();
			if (port < 0 || port > 65535) {
				port = 80;
				throw new Exception();
			}
		}
		catch (Exception exception){
			System.err.println("ChatRoom main : The port number must be an Integer between 0 and 65535!");
			// exception.printStackTrace();
		}
		// String room = "dic";
		ChatRoom cr = new ChatRoom(room, port);
		// System.out.println("");
		cr.start();
	}

}

