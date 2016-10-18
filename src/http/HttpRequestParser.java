package http;

import java.util.Set;

public class HttpRequestParser {

	private HttpRequest httpRequest;
	private boolean isParsedException;
	private boolean isParsingEnd;
	private int eNumber;
	private String eMessage;

	public HttpRequestParser(HttpRequest httpRequest) {
		this.isParsedException = false;
		this.isParsingEnd = false;
		this.eNumber = -1;
		this.eMessage = null;
		this.httpRequest = httpRequest;
	}

	public boolean isParsedException() {
		return isParsedException;
	}

	public void parseStartLine(String unParsedStartLine, boolean urlTooLong)
			throws Exception {
		if (urlTooLong) {
			exception(true, true, 414, "414 Request-URL Too Long");
		} else if (unParsedStartLine.split(" ").length != 3) {
			exception(true, true, 400, "400 Bad Request");
		} else {
			String[] startLine = unParsedStartLine.split(" ");
			if (startLine[0].equals("POST") || startLine[0].equals("GET")) {
				httpRequest.setMethod(startLine[0]);
				httpRequest.setRequestURI(startLine[1]);
				if (startLine[2].trim().equals("HTTP/1.1")) {
					httpRequest.setHttpVersion(startLine[2].trim());
				} else {
					exception(true, true, 400, "400 Bad Request");
				}
			} else {
				exception(true, true, 405, "405 Method Not Allowed");
			}
		}
	}

	public void parseHeadersLine(String unParsedHeadersLine, boolean urlTooLong)
			throws Exception {
		if (urlTooLong) {
			exception(true, true, 414, "414 Request-URL Too Long");
		} else {
			String[] headers = unParsedHeadersLine.trim().split("\r\n");
			for (String header : headers) {
				String[] methodsData = header.split(": ");
				if (methodsData.length != 2) {
					exception(true, true, 400, "400 Bad Request");
				} else {
					httpRequest.addHeader(methodsData[0], methodsData[1]);
				}
			}
			if (httpRequest.getMethod().equals("GET")) {
				isParsingEnd = true;
			}
			if (httpRequest.getMethod().equals("POST")) {
				Set<String> keys = httpRequest.getMethods().keySet();
				boolean isContentTypeExists = false;
				boolean isContentLengthExists = false;
				for (String key : keys) {
					if (key.equals("Content-Length")) {
						isContentLengthExists = true;
					} else if (key.equals("Content-Type")) {
						isContentTypeExists = true;
					}
				}
				if (!isContentLengthExists) {
					exception(true, true, 411, "411 Length Required");
				}
				if (!isContentTypeExists) {
					exception(true, true, 404, "404 Not Found");
				}
				if (isContentLengthExists) {
					try {
						String contentLength = httpRequest
								.getMethodValueByKey("Content-Length");
						int cntntLength = Integer.valueOf(contentLength);
						if (cntntLength < 0) {
							exception(true, true, 404, "404 Not Found");
						}
					} catch (NumberFormatException e) {
						exception(true, true, 404, "404 Not Found");
					}
				}
				if (isContentTypeExists) {
					String contentType = httpRequest
							.getMethodValueByKey("Content-Type");
					if (!contentType.equals("text/html")
							&& !contentType.equals("text/plain")
							&& !contentType.equals("application/octet-stream")
							&& !contentType.equals("application/json")
							&& !contentType
									.equals("application/x-www-form-urlencoded")) {
						exception(true, true, 415, "415 Unsupported Media Type");
					}
				}
			}
		}
	}

	public void exception(boolean isParsedException, boolean isParsingEnd,
			int eNumber, String eMessage) {
		this.isParsedException = isParsedException;
		this.isParsingEnd = isParsingEnd;
		this.eNumber = eNumber;
		this.eMessage = eMessage;
	}

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	public int geteNumber() {
		return eNumber;
	}

	public String geteMessage() {
		return eMessage;
	}

	public boolean isParsingEnd() {
		return isParsingEnd;
	}

}
