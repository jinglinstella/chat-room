package chat;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;


public class ChatClient extends JFrame implements Runnable {

	  DataOutputStream toServer = null;
	  DataInputStream fromServer = null;
	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private String serverName = "localhost";
	private int serverPort = 9898;
	private Socket socket;
	private JTextArea textArea;
	private JTextField chatField;
	
	public ChatClient() {
		super("Chat Client");
		this.setSize(ChatClient.WIDTH, ChatClient.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		createPanel();
		this.setVisible(true);
		
	}
	
	private void createPanel() {
		textArea = new JTextArea(40,20);
		chatField = new JTextField(40);
		chatField.addActionListener((e) -> { textArea.append(chatField.getText() + "\n"); 
													sendMessage();
													});
		this.add(textArea, BorderLayout.CENTER);
		this.add(chatField, BorderLayout.SOUTH);
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem connectItem = new JMenuItem("Connect");
		connectItem.addActionListener((e) -> connect(serverName, serverPort));
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> System.exit(0));
		menu.add(connectItem);
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
	private void connect(String serverName, int serverPort) {
		try {
		socket = new Socket("localhost", 9898);
		textArea.append("connected\n");

	      // Create an input stream to receive data from the server
	      fromServer = new DataInputStream(socket.getInputStream());

	      // Create an output stream to send data to the server
	      toServer = new DataOutputStream(socket.getOutputStream());
	      Thread t= new Thread(this);
	      t.start();
		} catch (Exception e) {
			
		}
	
	}
	
	public void run() {
		while (true) {
			try {
				String inString = fromServer.readUTF();
				textArea.append(inString + "\n");				
			} catch (IOException e) {
				System.err.println("cannot connect to server: " + e.getMessage());
				e.printStackTrace();
				socket = null;
				break;
			}
			
		}
	}
	
	private void sendMessage() {
		if (socket == null) {
			textArea.append("cannot send anything. not connected\n");
		} else {
			try {
			toServer.writeUTF(chatField.getText());
			toServer.flush();
			} catch (Exception e) {
				textArea.append("error sending message\n");
				System.err.println("error sending message");
			}
			
		}
		chatField.setText("");
	}
	
	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
	}
}
