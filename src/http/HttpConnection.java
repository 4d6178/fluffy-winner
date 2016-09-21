package http;

import java.net.Socket;

public class HttpConnection {
	private Socket socket;
	private int timeout;
	
	public HttpConnection(Socket socket, int timeout){
		this.socket = socket;
		this.timeout = timeout;
	}	
	
}
