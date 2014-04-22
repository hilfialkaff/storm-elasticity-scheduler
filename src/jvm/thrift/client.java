package storm.scheduler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Object;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class client extends Thread{

	private BufferedReader in;
	private PrintWriter out;
	private String hostname;
	private int port;
	private Socket socket;
	private Queue<String> MsgQueue = new LinkedBlockingQueue<String>();

	public client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		try {
			this.socket = new Socket(this.hostname, this.port);
			this.in = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Exception: "+e);
		}
		System.out.println("metrics client started!!!");

	}
	
	public void addMsg(String msg)
	{
		
		//try{
			//this.MsgQueue.wait();
			this.MsgQueue.add(msg);
			//System.out.println("msg of length "+Integer.toString(msg.length())+ " MsgQueue size: "+Integer.toString(this.MsgQueue.size()));
			//this.notify();
		//}
		//catch(java.lang.InterruptedException e)
		//{
			//System.out.println(e);
		//}
		
	}

	public void run() {
		//System.out.println("In Run!!!");

		while (true) {
			//System.out.println("loop "+Integer.toString(this.MsgQueue.size()));
			
			if(this.MsgQueue.size()>0) {
				//try{
					//this.MsgQueue.wait();
					//System.out.println("sending msg to: "+ this.socket.getRemoteSocketAddress());
					this.out.println(this.MsgQueue.poll());
					//this.notify();
				//}
				//catch(java.lang.InterruptedException e)
				//{
					//System.out.println(e);
				//}
			}
			
			try{
			Thread.sleep(100);
			}catch(java.lang.InterruptedException e)
			{
				
			}
			
		
		}
	}

}
