package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.ws.http.HTTPException;

import server.HttpServer;

public class SocketProcessor implements Runnable {

	private int DEFAULT_BUFFER_SIZE = 1;

	private Socket socket;
	private int timeout;
	private InputStream inputStream;
	private OutputStream outputStream;

	public SocketProcessor(Socket socket, int timeout) {
		this.socket = socket;
		this.timeout = timeout;
	}

	public void run() {
		try {
			while (true) {
				this.inputStream = socket.getInputStream();
				HttpRequestParser requestParser = new HttpRequestParser();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				InputStreamReader isr = new InputStreamReader(bis);
				char[] buf = new char[DEFAULT_BUFFER_SIZE];
				while (true) {
					StringBuffer process = new StringBuffer();
					isr.read(buf);
					// System.out.print(buf[0]);
					process.append(buf[0]);
					try {
						requestParser.getNext(process.toString());
						if (requestParser.isParsingEnd()
								|| requestParser.isParsedException()) {
							break;
						}
					} catch (HTTPException e) {
						// work with exception
						// may should create response to client
					}
					// buf.clear();
				}

				this.outputStream = socket.getOutputStream();

				if (requestParser.isParsingEnd()
						&& !requestParser.isParsedException()) {
					if (requestParser.getHttpRequest().getMethod()
							.equals("GET")) {

						BufferedOutputStream bos = new BufferedOutputStream(
								outputStream);
						OutputStreamWriter osr = new OutputStreamWriter(bos);
						HttpRequest httpRequest = requestParser
								.getHttpRequest();
						String object = HttpServer.clientsData.get(httpRequest
								.getRequestURI());
						HttpResponse httpResponse = new HttpResponse();
						if (null != object) {
							httpResponse
									.setResponseStartLine("HTTP/1.1 200 OK");
							httpResponse.addHeader("Date",
									(new Date()).toString());
							httpResponse.addHeader("Server", "Tosha");
							httpResponse.addHeader("Content-Length", "4");
							httpResponse
									.addHeader("Content-Type", "text/plain");
							httpResponse.addHeader("Connection", "Keep-Alive");
							// httpResponse.addHeader("Connection:", "Closed");

							httpResponse.setMessageBody(object);
						} else {
							httpResponse
									.setResponseStartLine("HTTP/1.1 404 Bad Request");
						}

						// httpResponse.setMessageBody("<html>" + "<body>"
						// + "<h1>Hello, World!</h1>" + "</body>" +
						// "</html>\n");
						System.out.println(httpResponse.generateResponse());
						osr.write(httpResponse.generateResponse());

						/*
						 * osr.write("HTTP/1.1 200 OK\n" +
						 * "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
						 * "Server: Apache/2.2.14 (Win32)\n" +
						 * "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
						 * "Content-Length: 88\n" + "Content-Type: text/html\n"
						 * + "Connection: Closed\r\n\r\n" + "<html>" + "<body>"
						 * + "<h1>Hello, World!</h1>" + "</body>" +
						 * "</html>\n");
						 */
						osr.flush();
						// ObjectOutputStream oos = new ObjectOutputStream(
						// socket.getOutputStream());
						// oos.writeObject("HTTP/1.1 200 OK");
						// oos.close();
						osr.close();
					} else if (requestParser.getHttpRequest().getMethod()
							.equals("POST")) {
						HttpRequest httpRequest = requestParser
								.getHttpRequest();
						HttpServer.clientsData.put(httpRequest.getRequestURI(),
								httpRequest.getMessageBody());
						BufferedOutputStream bos = new BufferedOutputStream(
								outputStream);
						OutputStreamWriter osr = new OutputStreamWriter(bos);
						HttpResponse httpResponse = new HttpResponse();
						httpResponse.setResponseStartLine("HTTP/1.1 200 OK");
						httpResponse.addHeader("Connection:", "Keep-Alive");
						// httpResponse.addHeader("Date", (new
						// Date()).toString());
						// httpResponse.addHeader("Server", "Tosha");
						// httpResponse.addHeader("Content-Length", "4");
						// httpResponse.addHeader("Content-Type", "text/html");
						// httpResponse.addHeader("Connection:", "Closed");

						// httpResponse.setMessageBody(requestParser.getHttpRequest()
						// .getMessageBody());
						//
						// System.out.println(httpResponse.generateResponse());
						osr.write(httpResponse.generateResponse());
						osr.flush();
						osr.close();
					}

					Set<Entry<String, String>> dataMap = HttpServer.clientsData.entrySet();
					for (Entry<String, String> entry : dataMap) {
						System.out.println(entry);
					}
					System.out.println("______");
				}

			}
			// requestParser.getHttpRequest().toString();
			// requestParser.getUnParsedData().toString();
		} catch (Exception e) {
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