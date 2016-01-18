package server;

import http.HttpRequestParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;

import exception.HttpRequestException;

public class HttpServer {

	private int port;
	private int timeout = 1000; // ms
	private int DEFAULT_BUFFER_SIZE = 1;

	public HttpServer(int port) {
		this.port = port;
	}

	public void serve() {
		try {
			if (port > 0 && port <= 65535) {
				ServerSocket serverSocket = new ServerSocket(port);
				while (true) {
					Socket socket = serverSocket.accept();
					new Thread(new SocketProcessor(socket, timeout)).start();
				}
			} else {
				System.out
						.println("Error. Port should place in range [1, 65535]");
			}
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}

	private class SocketProcessor implements Runnable {

		private Socket socket;
		private int timeout;
		private InputStream inputStream;
		private OutputStream outputStream;

		private SocketProcessor(Socket socket, int timeout) {
			this.socket = socket;
			this.timeout = timeout;

		}

		public void run() {

			try {
				this.inputStream = socket.getInputStream();

				HttpRequestParser requestParser = new HttpRequestParser();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				InputStreamReader isr = new InputStreamReader(bis);
				char[] buf = new char[DEFAULT_BUFFER_SIZE];
				while (true) {
					StringBuffer process = new StringBuffer();
					isr.read(buf);
					process.append(buf[0]);
					try {
						if (!requestParser.getNext(process.toString())) {
							break;
						}
					} catch (HttpRequestException e) {
						// work with exception
						// may should create response to client
					}

					// buf.clear();
				}
				
				requestParser.getHttpRequest().toString();
			} catch (IOException e) {
				System.out.println("Exception: " + e.getMessage());
			} finally {
				try {
					socket.close();
				} catch (Throwable t) {
					/* do nothing */
				}
			}

		}
	}

}
