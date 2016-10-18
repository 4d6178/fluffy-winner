package server;

public class MainClass {

	public static void main(String[] args) {

		HttpServer httpServer = new HttpServer(8080);
		try {
			httpServer.serve();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
