package storm.scheduler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Queue;
import java.util.*;  
import java.util.concurrent.LinkedBlockingQueue;

public class server extends Thread {

	private int port;

	private ServerSocket listener;

	private static HashSet<String> names = new HashSet<String>();

	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	
	private List<Handler>clientHandlers = new ArrayList<Handler>();
	
	public Queue<String> MsgQueue = new LinkedBlockingQueue<String>();
	

	public server(int port) {
		this.port = port;
		 
		
	}

	public void run() {
		System.out.println("The server is running.");

		try {
			this.listener = new ServerSocket(this.port);
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		try {
			while (true) {
				Handler h = new Handler(listener.accept(), this.MsgQueue);
				h.start();
				this.clientHandlers.add(h);
				
				
				
			}
		} catch (java.io.IOException e) {

			System.out.println(e);
		} finally {
			try {
				listener.close();
			} catch (java.lang.Exception e) {
				System.out.println(e);
			}
		}

	}
	
	public String getMsg()
	{
		// System.out.println("size of MsgQueue: "+Integer.toString(this.MsgQueue.size()));
		return this.MsgQueue.poll();
		
	}

	private class Handler extends Thread {
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Queue<String> Msg_Queue;
		
		

		public Handler(Socket socket, Queue<String> MsgQueue) {
			this.socket = socket;
			this.Msg_Queue = MsgQueue;
		}

		public void run() {
			try {
				
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(
						this.socket.getInputStream()));
				out = new PrintWriter(this.socket.getOutputStream(), true);

				while (true) {
					String input = in.readLine();
					if (input == null) {
						return;
					}
					
					//System.out.println("msg added: "+Integer.toString(Msg_Queue.size()));
					Msg_Queue.add(input);
					
					/*
					System.out.println(this.socket.getRemoteSocketAddress()
							+ " :" + input);
					*/		
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {

				try {
					socket.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
	}
/*
	public static void main(String[] args) throws Exception {

		server s = new server(9001);
		s.start();

		while (true);

	}
	*/
}
