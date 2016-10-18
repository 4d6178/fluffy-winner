package server;

import http.SocketProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

	private int port;
	private int timeout = 100; // ms
	public static Map<String, String> clientsData;

	private int DEFAULT_PORT = 8080;

	public HttpServer(int port) {
		if (port > 0 && port <= 65535) {
			this.port = port;
		} else {
			this.port = DEFAULT_PORT;
		}
	}

	public void serve() throws InterruptedException {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			clientsData = new HashMap<String, String>();
			while (true) {
				Socket socket = serverSocket.accept();
				if (null != socket) {
					Thread t = new Thread(new SocketProcessor(socket));
					t.start();
					Thread s = new Thread(new Timer(timeout, t));
					s.setDaemon(true);
					s.start();
				}
			}
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	private class Timer implements Runnable{
		
		private int timeout;
		private Thread t;
		
		Timer(int timeout, Thread t){
			this.timeout = timeout;
			this.t = t;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(timeout);
				if(t.isAlive()){
					t.interrupt();
				}
			} catch (InterruptedException e) {
				System.out.println(e);
			}
			
		}
		
	}

}
