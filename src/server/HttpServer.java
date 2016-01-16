package server;

import http.HttpConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

	private int port;
	private int timeout = 1000; //ms

	public HttpServer(int port) {
		this.port = port;
	}

	public void serve() throws IOException {
		if (port > 0 && port <= 65535) {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				HttpConnection connection = new HttpConnection(socket, timeout);
				new Thread(new SocketProcessor(connection)).start();
			}
		} else {
			System.out.println("Error. Port should place in range [1, 65535]");
		}
	}

	private class SocketProcessor implements Runnable {

		HttpConnection connection;

		private SocketProcessor(HttpConnection connection) {
			this.connection = connection;
		}

		public void run() {

		}
	}

}
