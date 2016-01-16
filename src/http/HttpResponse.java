package http;

public class HttpResponse {

	private String header;
	private String contentType;
	private String contentLength;
	private String code;

	public String getHeader() {
		return header;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContentLength() {
		return contentLength;
	}

	public String getCode() {
		return code;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentLength(String contentLength) {
		this.contentLength = contentLength;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
