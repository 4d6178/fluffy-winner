package http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
			inputStream = socket.getInputStream();
			HttpRequestReader httpRequestReader = new HttpRequestReader();
			while (true) {
				readInputData(httpRequestReader);
				addAddiction(httpRequestReader);
				generateAnswer(httpRequestReader);
				inputStream.close();
				socket.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (Throwable t) {
				System.out.println(t.getMessage());
			}
		}

	}

	private void readInputData(HttpRequestReader httpRequestReader)
			throws Exception {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		InputStreamReader isr = new InputStreamReader(bis);
		char[] buf = new char[DEFAULT_BUFFER_SIZE];
		while (true) {
			StringBuffer process = new StringBuffer();
			isr.read(buf);
			if (null != buf && buf.length > 0) {
				process.append(buf[0]);
				System.out.print(buf[0]);
				try {
					httpRequestReader.getNext(process.toString());
					if (httpRequestReader.isParsingEnd()
							|| httpRequestReader.isParsedException()) {
						break;
					}
				} catch (HTTPException e) {
					System.out.println(e.getMessage());
				} 
			}
		}

	}

	private void addAddiction(HttpRequestReader httpRequestReader) {
		if (!httpRequestReader.isParsedException()) {
			if (null == HttpServer.clientsData.get(httpRequestReader
					.getHttpRequest().getRequestURI())) {
				HttpServer.clientsData.put(httpRequestReader.getHttpRequest()
						.getRequestURI(), httpRequestReader.getHttpRequest()
						.getMessageBody());
			}
		}
	}

	private void generateAnswer(HttpRequestReader httpRequestReader)
			throws IOException {
		HttpResponse httpResponse = new HttpResponse();
		outputStream = socket.getOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(outputStream);
		OutputStreamWriter osw = new OutputStreamWriter(bos);
		HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator(
				httpResponse, httpRequestReader);
		osw.write(httpResponseGenerator.getResponse());
		
		osw.flush();
		osw.close();
		outputStream.close();
	}
}