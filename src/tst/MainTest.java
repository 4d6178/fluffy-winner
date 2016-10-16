package tst;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.Test;

public class MainTest {

	int serverPort = 8080;
	String address = "127.0.0.1";

	InetAddress ipAddress;
	Socket socket;

	InputStream sin;
	OutputStream sout;

	OutputStreamWriter osw;
	BufferedInputStream bis;
	InputStreamReader isr;

	public void connect() throws IOException {
		ipAddress = InetAddress.getByName(address);
		System.out.println("Connect to  " + address + ":" + serverPort);
		socket = new Socket(ipAddress, serverPort);

		sin = socket.getInputStream();
		sout = socket.getOutputStream();

		osw = new OutputStreamWriter(sout);
		bis = new BufferedInputStream(sin);
		isr = new InputStreamReader(bis);
	}

	public void send(String requestMessage) throws IOException {
		osw.write(requestMessage);
		System.out.println("Sending this line to the server...");
		osw.flush();
	}

	public String receive() throws IOException {
		char[] buf = new char[1];
		String response = "";
		while (true) {
			int i = isr.read(buf);
			if (i == -1) {
				break;
			}
			response += buf[0];
		}
		System.out.println(response);
		System.out.print("end");

		return response;
	}

	public void close() throws IOException {
		osw.close();
		isr.close();
		socket.close();
	}

	public String testConnect(String rqst) {
		String response = null;
		try {
			connect();
			send(rqst);
			response = receive();
			close();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return response;
	}

	@Test
	public void test() {
		String str = "POST /ololo HTTP/1.1\r\n"
				+ "User-Agent: curl/7.35.0\r\n"
				+ "Host: localhost:8080\r\n"
				+ "Accept: */*\r\n"
				+ "Content-Length: 5\r\n"
				+ "Content-Type: application/x-www-form-urlencoded\r\n\r\nololo";
		String response = testConnect(str);
		assertNotNull(response);
		assertTrue(response.trim().equals("HTTP/1.1 200 OK"));
	}
}
