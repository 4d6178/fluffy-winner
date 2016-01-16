package http;

import java.net.Socket;

public class HttpConnection {
	private Socket socket;
	private int timeout;
	
	public HttpConnection(Socket socket, int timeout){
		this.socket = socket;
		this.timeout = timeout;
	}

	public Socket getSocket() {
		return socket;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	
}
