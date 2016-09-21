package http;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpRequest {

	private String method;
	private String requestURI;
	private String httpVersion;
	private String messageBody;
	private boolean isHttpRequestWithError = true;

	private Map<String, String> methods = new HashMap<String, String>();

	public HttpRequest() {

	}
	
	public String toString(){
		System.out.println("method = " + method);
		System.out.println("requestURI = " + requestURI);
		System.out.println("httpVersion = " + httpVersion);
		
		Set<Entry<String, String>> sets = methods.entrySet();
		for(Entry<String, String> set : sets){
			System.out.println(set.getKey() + " " + set.getValue());
		}
		
		System.out.println("body " + messageBody);
		
		return null;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public Map<String, String> getMethods() {
		return methods;
	}
	
	public String getMethodValueByKey(String key) {
		return methods.get(key);
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

	public void addHeader(String key, String value) {
		this.methods.put(key, value);
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	public boolean isHttpRequestWithError() {
		return isHttpRequestWithError;
	}

	public void setHttpRequestWithError(boolean isHttpRequestWithError) {
		this.isHttpRequestWithError = isHttpRequestWithError;
	}


}
