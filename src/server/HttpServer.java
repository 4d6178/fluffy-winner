package server;

import http.SocketProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

	private int port;
	private int timeout = 1000; // ms
	public static Map<String, String> clientsData;

	private int DEFAULT_PORT = 8080;

	public HttpServer(int port) {
		if (port > 0 && port <= 65535) {
			this.port = port;
		} else {
			this.port = DEFAULT_PORT;
		}
	}

	public void serve() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			clientsData = new HashMap<String, String>();
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new SocketProcessor(socket, timeout)).start();
			}
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}

}
