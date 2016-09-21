package http;

public class HttpRequestParser {

	HttpRequest httpRequest;

	private String doubleNewLine;
	private String unParsedData;
	private boolean isParsedException = false;
	private boolean isParsingEnd = false;

	private int eNumber = -1;

	private enum fsmState {
		One, Two, Three, Four
	};

	private int MAX_START_LINE_SiZE = 1024;
	private int MAX_HEADERS_SIZE = 1024;
	private int MAX_BODY_SIZE = 2048;

	private boolean isStartLine;
	private fsmState state = fsmState.One;
	private boolean isMethod;
	private boolean isBody;

	private int contentRead;
	private int startLineSize;
	private int headersLineSize;
	private int headersNewLineCount;
	private int bodyReadCount;

	public HttpRequestParser() {
		this.isStartLine = true;
		this.unParsedData = "";
		this.doubleNewLine = "   ";
		this.startLineSize = 0;
		this.headersLineSize = 0;
		this.headersNewLineCount = 0;
		this.bodyReadCount = 0;
		this.httpRequest = new HttpRequest();
	}

	public void getNext(String data) throws Exception {
		if (state == fsmState.One) {
			readStartLine(data);
		} else if (state == fsmState.Two) {
			readHeadersLine(data);
		} else if (state == fsmState.Three) {
			if (httpRequest.getMethod().equals("POST")) {
				String contentSize = httpRequest
						.getMethodValueByKey("Content-Length");
				int contentLength = 0;
				if (null != contentSize) {
					contentLength = Integer.parseInt(contentSize);
				}
				if (contentLength > 0 && contentLength < MAX_BODY_SIZE) {
					readBody(data, contentLength);

					//httpRequest.setHttpRequestWithError(false);
				}
			}
		}
	}

	/*
	 * unParsedData += data; if (!data.equals("\n") && !isBody) { if (isMethod)
	 * { doubleNewLine = doubleNewLine.substring(1, 3) + data; } return true; }
	 * else if ((isBody && contentRead < contentLength)) { contentRead++; if
	 * (contentRead == contentLength) {
	 * httpRequest.setMessageBody(unParsedData); return false; } return true; }
	 * else { if (parse(data)) { return true; } }
	 */

	public boolean isParsedException() {
		return isParsedException;
	}

	private void readStartLine(String data) throws Exception {
		if (startLineSize < MAX_START_LINE_SiZE && !data.equals("\n")) {
			unParsedData += data;
		} else {
			parseStartLine(unParsedData);
		}
	}

	private void readHeadersLine(String data) throws Exception {
		if (data.equals("\n") || data.equals("\r")) {
			headersNewLineCount += 1;
		} else {
			headersNewLineCount = 0;
		}
		if (headersLineSize < MAX_HEADERS_SIZE && headersNewLineCount < 4) {
			unParsedData += data;
		} else {
			parseHeadersLine(unParsedData);
			unParsedData = "";
		}

	}

	private void readBody(String data, int contentLenght) {
		if (bodyReadCount < contentLenght) {
			bodyReadCount += 1;
			unParsedData += data;
			if (bodyReadCount == contentLenght) {
				httpRequest.setMessageBody(unParsedData);
				// httpRequest.toString();
				isParsingEnd = true;
			}
		}
	}

	private boolean parseHeadersLine(String unParsedHeadersLine)
			throws Exception {
		String[] headers = unParsedHeadersLine.trim().split("\r\n");
		for (String header : headers) {
			String[] methodsData = header.split(": ");
			if (methodsData.length != 2) {
				isParsedException = true;
				return false;
			} else {
				httpRequest.addHeader(methodsData[0], methodsData[1]);
			}

		}
		if (httpRequest.getMethod().equals("POST")) {
			state = fsmState.Three;
		} else {
			isParsingEnd = true;
		}

		return true;
	}

	private boolean parseStartLine(String unParsedStartLine) throws Exception {
		if (unParsedStartLine.split(" ").length != 3) {
			isParsedException = true;
			isParsingEnd = true;
			System.out.println("Error in start line");
		} else {
			String[] start = unParsedData.split(" ");
			if (start[0].equals("POST") || start[0].equals("GET")) {
				httpRequest.setMethod(start[0]);
				httpRequest.setRequestURI(start[1]);
				if (start[2].equals("HTTP/0.9") || start[2].equals("HTTP/1.0")
						|| start[2].trim().equals("HTTP/1.1")) {
					httpRequest.setHttpVersion(start[2]);
					state = fsmState.Two;
					unParsedData = "";

					return true;
				} else {
					throw new Exception("http version");
					// Exception, incorrect http version
				}
			} else {
				throw new Exception("method");
				// Exception, incorrect method

			}

		}
		return false;
	}

	// private boolean parse(String data) throws HTTPException { if
	// (isStartLine) { if (!parseHeader()) { return false; // error, bad start
	// line } unParsedData = ""; isStartLine = false; isMethod = true;
	// doubleNewLine = doubleNewLine.substring(1, 3) + data; return true; } else
	// if (isMethod) { if (data.equals("\n") && doubleNewLine.equals("\r\n\r"))
	// { if (!parseMethods()) { return false; } if
	// (httpRequest.getMethods().containsKey("Content-Length")) { contentLength
	// = Integer.parseInt(httpRequest .getMethodValueByKey("Content-Length")); }
	//
	// if (contentLength <= 0) { return false; } unParsedData = ""; isMethod =
	// false; isBody = true; } doubleNewLine = doubleNewLine.substring(1, 3) +
	// data; return true; } return false; }

	// private boolean parseHeader() throws HTTPException { if
	// (unParsedData.equals("") || unParsedData.split(" ").length != 3) { throw
	// new HTTPException(400); } else { // need to check methods. work only with
	// post and set. users // answer may consists another methods or not
	// available methods String[] start = unParsedData.split(" ");
	// httpRequest.setMethod(start[0]); httpRequest.setRequestURI(start[1]);
	// httpRequest.setHttpVersion(start[2]); return true; }

	// return false; }

	// private boolean parseMethods() throws HTTPException {
	// if (unParsedData.equals("")) {
	// throw new HTTPException(400);
	// }
	// String[] methodLine = unParsedData.split("\r\n");
	// for (String methodLn : methodLine) {
	// String[] method = methodLn.split(": ");
	// if (method.length == 2) {
	// httpRequest.addHeader(method[0], method[1]);
	// } else {
	// return false;
	// }
	// }
	// return true;
	// }

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	public int geteNumber() {
		return eNumber;
	}

	public String getUnParsedData() {
		return unParsedData;
	}

	public boolean isParsingEnd() {
		return isParsingEnd;
	}

}
