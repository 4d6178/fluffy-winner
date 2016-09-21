package http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponse {

	private String responseStartLine;
	private Map<String, String> headers = new LinkedHashMap<String, String>();
	private String messageBody;

	private String header;
	private String code;

	public String getHeader() {
		return header;
	}

	public String getCode() {
		return code;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getResponseStartLine() {
		return responseStartLine;
	}

	public void setResponseStartLine(String responseStartLine) {
		this.responseStartLine = responseStartLine;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	private String getNewLine() {
		return "\n";
	}

	private String getBodySeparator() {
		return "\r\n";
	}

	public String generateResponse() {
		String response = "";
		response += getResponseStartLine() + getNewLine();
		Set<Entry<String, String>> set = headers.entrySet();
		for (Entry<String, String> entry : set) {
			response += entry.getKey() + ":" + entry.getValue() + getNewLine();
		}
		response += getBodySeparator() + getMessageBody() + getNewLine();
		return response;

	}

}
