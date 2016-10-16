package http;

import java.io.IOException;
import java.util.Date;

import server.HttpServer;

public class HttpResponseGenerator {

	private HttpResponse httpResponse;
	private HttpRequestReader httpRequestReader;

	HttpResponseGenerator(HttpResponse httpReponse,
			HttpRequestReader httpRequestReader) {
		this.httpResponse = httpReponse;
		this.httpRequestReader = httpRequestReader;
	}

	public String getResponse() throws IOException {
		int eNumber = httpRequestReader.geteNumber();
		String eMessage = httpRequestReader.geteMessage();

		if (eNumber != -1) {
			generateResponseStartLine(eMessage);
		} else {
			generateResponse(eMessage);
		}
		
		return httpResponse.generateResponse();

	}

	private void generateResponse(String messageCode){
		if (httpRequestReader.getHttpRequest().getMethod().equals("GET")) {
			generateResponsePackage("tosha");
		} else {
			generateResponseStartLine("200 OK");
		}
		
	}

	private void generateResponseStartLine(String responseCode) {
		httpResponse.setResponseStartLine(httpRequestReader.getHttpRequest()
				.getHttpVersion() + " " + responseCode);
	}

	private void generateResponsePackage(String serverName) {
		HttpRequest httpRequest = httpRequestReader.getHttpRequest();
		String object = HttpServer.clientsData.get(httpRequest.getRequestURI());

		if (null != object) {
			httpResponse.setResponseStartLine("HTTP/1.1 200 OK");
			httpResponse.addHeader("Date", (new Date()).toString());
			httpResponse.addHeader("Server", serverName);
			httpResponse.addHeader("Content-Length", object.length() + 1 + "");
			httpResponse.addHeader("Content-Type", "text/plain");
			httpResponse.addHeader("Connection", "Keep-Alive");

			httpResponse.setMessageBody(object + "\n");
		} else {
			generateResponseStartLine("404 Bad Request");
		}
	}

}
