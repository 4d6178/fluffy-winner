package server;

public class MainClass {

	public static void main(String[] args) {

		HttpServer httpServer = new HttpServer(8080);
		httpServer.serve();
		
	}

}
