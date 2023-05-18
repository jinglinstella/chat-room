package chat;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;



public class ChatServer extends JFrame implements Runnable {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private JTextArea ta;
	private static int clientNo = 0;
	private ArrayList<HandleAClient> clients = new ArrayList<>();
	
	public ChatServer() {
		super("Chat Server");
		this.setSize(ChatServer.WIDTH, ChatServer.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		createPanel();
		Thread t = new Thread(this);
		t.start();
		this.setVisible(true);
		
	}
	
	private void createPanel() {
		ta = new JTextArea(40,30);
		this.add(ta, BorderLayout.CENTER);
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	

	  public void run() {
		  try {
	        // Create a server socket
	        ServerSocket serverSocket = new ServerSocket(9898);
	        ta.append("Chat server started at " 
	          + new Date() + '\n');
	    
	        while (true) {
	          // Listen for a new connection request
	          Socket socket = serverSocket.accept();
	    
	          // Increment clientNo
	          clientNo++;
	          
	          ta.append("Starting thread for client " + clientNo +
	              " at " + new Date() + '\n');

	            // Find the client's host name, and IP address
	            InetAddress inetAddress = socket.getInetAddress();
	            ta.append("Client " + clientNo + "'s host name is "
	              + inetAddress.getHostName() + "\n");
	            ta.append("Client " + clientNo + "'s IP Address is "
	              + inetAddress.getHostAddress() + "\n");
	          
	          // Create and start a new thread for the connection
	            HandleAClient newClient = new HandleAClient(socket, clientNo);
	            clients.add(newClient);
	          new Thread(newClient).start();
	        }
	      }
	      catch(IOException ex) {
	        System.err.println(ex);
	      }
		    
	  }
	
	public static void main(String[] args) {
		ChatServer chatServer = new ChatServer();
	}
	

	// Define the thread class for handling new connection
	class HandleAClient implements Runnable {
	  private Socket socket; // A connected socket
	  private int clientNum;
	  private DataOutputStream outputToClient;
	  
	  /** Construct a thread */
	  public HandleAClient(Socket socket, int clientNum) {
	    this.socket = socket;
	    this.clientNum = clientNum;
	  }

	  public DataOutputStream getOutputStream() {
		  return outputToClient;
		  
	  }
	  
	  /** Run a thread */
	  public void run() {
	    try {
	      // Create data input and output streams
	      DataInputStream inputFromClient = new DataInputStream(
	        socket.getInputStream());
	      outputToClient = new DataOutputStream(
	        socket.getOutputStream());

	      // Continuously serve the client
	      while (true) {
	        // Receive radius from the client
	        String message = inputFromClient.readUTF();

	        for (HandleAClient client : clients) {
	        	if (client == this) {
	        		continue;
	        	} else {
	        		client.getOutputStream().writeUTF(clientNum +": " + message);
	        	}
	        }
	        
	      }
	    }
	    catch(IOException ex) {
	    	clients.remove(this);
	      ex.printStackTrace();
	    }
	  }
	}
}


