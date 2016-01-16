package http;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpRequest {

	private String method;
	private String requestURI;
	private String httpVersion;
	private String messageBody;
	private Map<String, String> headers;
	private Set<String> methods = new HashSet<String>();

	public HttpRequest() {

	}

	public String getMessageBody() {
		return messageBody;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getMethod() {
		return method;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

}
