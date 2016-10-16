package http;

public class HttpRequestReader {

	private HttpRequest httpRequest;
	private HttpRequestParser httpRequestParser;

	private String unParsedData;
	private boolean isParsedException;
	private boolean isParsingEnd;
	private int contentLength;
	private boolean urlTooLong;

	public enum fsmState {
		START_LINE, HEADER_LINE, BODY
	};

	private int MAX_START_LINE_SIZE = 1024;
	private int MAX_HEADERS_SIZE = 1024;
	private int MAX_BODY_SIZE = 2048;

	private fsmState state;

	public HttpRequestReader() {
		this.unParsedData = "";
		this.contentLength = 0;
		this.isParsedException = false;
		this.isParsingEnd = false;
		this.urlTooLong = false;
		this.state = fsmState.START_LINE;
		this.httpRequest = new HttpRequest();
		this.httpRequestParser = new HttpRequestParser(httpRequest);
	}

	public void getNext(String data) throws Exception {
		if (state == fsmState.START_LINE) {
			readStartLine(data);
		} else if (state == fsmState.HEADER_LINE) {
			readHeadersLine(data);
		} else if (state == fsmState.BODY) {
			if (httpRequest.getMethod().equals("POST")) {
				readBody(data);
			}
		}
	}

	private void readStartLine(String data) throws Exception {
		if (unParsedData.length() > MAX_START_LINE_SIZE && !data.equals("\n")) {
			urlTooLong = true;
		} else {
			unParsedData += data;
		}
		if (urlTooLong || data.equals("\n")) {
			httpRequestParser.parseStartLine(unParsedData, urlTooLong);
			isParsedException = httpRequestParser.isParsedException();
			isParsingEnd = httpRequestParser.isParsingEnd();
			if (isParsedException != true && isParsingEnd != true) {
				state = fsmState.HEADER_LINE;
				unParsedData = "";
			}
		}
	}

	private void readHeadersLine(String data) throws Exception {
		if (unParsedData.length() > MAX_HEADERS_SIZE) {
			urlTooLong = true;
		} else {
			unParsedData += data;
		}
		if (unParsedData.contains("\r\n\r\n") || urlTooLong) {
			httpRequestParser.parseHeadersLine(unParsedData, urlTooLong);
			isParsedException = httpRequestParser.isParsedException();
			isParsingEnd = httpRequestParser.isParsingEnd();
			if (!isParsedException) {
				state = fsmState.BODY;
				unParsedData = "";
				if (httpRequest.getMethod().equals("POST")) {
					String cntLength = httpRequest
							.getMethodValueByKey("Content-Length");
					if (null != cntLength && !cntLength.equals("")) {
						contentLength = Integer.valueOf(cntLength);
					}
				}
			}
		}

	}

	private void readBody(String data) {
		if (contentLength > 0 && contentLength < MAX_BODY_SIZE) {
			if (unParsedData.length() < contentLength) {
				unParsedData += data;
				if (unParsedData.length() == contentLength) {
					httpRequest.setMessageBody(unParsedData);
					isParsingEnd = true;
				}
			}
		}

	}

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	public int geteNumber() {
		return httpRequestParser.geteNumber();
	}

	public String geteMessage() {
		return httpRequestParser.geteMessage();
	}

	public String getUnParsedData() {
		return unParsedData;
	}

	public boolean isParsingEnd() {
		return isParsingEnd;
	}

	public boolean isParsedException() {
		return isParsedException;
	}

}
