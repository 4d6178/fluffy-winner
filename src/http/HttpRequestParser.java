package http;

import java.util.Map;

import exception.HttpRequestException;

public class HttpRequestParser {

	HttpRequest httpRequest;

	private String doubleNewLine;
	private String unParsedData;

	private boolean isStartLine;
	private boolean isMethod;
	private boolean isBody;

	private int contentLength;
	private int contentReaden;

	public HttpRequestParser() {
		isStartLine = true;
		this.unParsedData = "";
		this.doubleNewLine = "   ";
		this.contentLength = -1;
		httpRequest = new HttpRequest();
	}

	public boolean getNext(String data) throws HttpRequestException {
		unParsedData += data;
		if (!data.equals("\n") && !isBody) {
			if (isMethod) {
				doubleNewLine = doubleNewLine.substring(1, 3) + data;
			}
			return true;
		} else if ((isBody && contentReaden < contentLength)) {
			contentReaden++;
			if (contentReaden == contentLength) {
				httpRequest.setMessageBody(unParsedData);
				return false;
			}
			return true;
		} else {
			if (parse(data)) {
				return true;
			}
		}
		return false;
	}

	private boolean parse(String data) throws HttpRequestException {
		if (isStartLine) {
			if (!parseHeader()) {
				return false; // error, bad start line
			}
			unParsedData = "";
			isStartLine = false;
			isMethod = true;
			doubleNewLine = doubleNewLine.substring(1, 3) + data;
			return true;
		} else if (isMethod) {
			if (data.equals("\n") && doubleNewLine.equals("\r\n\r")) {
				if (!parseMethods()) {
					return false;
				}
				if (httpRequest.getMethods().containsKey("Content-Length")) {
					contentLength = Integer.parseInt(httpRequest
							.getMethodValueByKey("Content-Length"));
				}
				if (contentLength <= 0) {
					return false;
				}
				unParsedData = "";
				isMethod = false;
				isBody = true;
			}
			doubleNewLine = doubleNewLine.substring(1, 3) + data;
			return true;
		}
		return false;
	}

	private boolean parseHeader() throws HttpRequestException {
		if (unParsedData.equals("") || unParsedData.split(" ").length != 3) {
			throw new HttpRequestException(400);
		} else {
			// need to check methods. work only with post and set. users
			// answer may consists another methods or not available methods
			String[] start = unParsedData.split(" ");
			httpRequest.setMethod(start[0]);
			httpRequest.setRequestURI(start[1]);
			httpRequest.setHttpVersion(start[2]);
			return true;
		}

		// return false;
	}

	private boolean parseMethods() throws HttpRequestException {
		if (unParsedData.equals("")) {
			throw new HttpRequestException(400);
		}
		String[] methodLine = unParsedData.split("\r\n");
		for (String methodLn : methodLine) {
			String[] method = methodLn.split(": ");
			if (method.length == 2) {
				httpRequest.addHeader(method[0], method[1]);
			} else {
				return false;
			}
		}
		return true;
	}

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

}
