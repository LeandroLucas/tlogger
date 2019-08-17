/**
 * 
 */
package com.lebittec.tlogger.models;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 *
 */
public class HttpConnection {

	private HttpURLConnection connection;

	public HttpConnection(String url) throws IOException {
		connection = this.openConnection(url);
	}

	private HttpURLConnection openConnection(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		return con;
	}

	public void addParameters(String urlParameters) throws IOException {
		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.write(urlParameters.getBytes("UTF-8"));
		wr.flush();
		wr.close();
	}

	public Object execute() throws IOException {
		return connection.getContent();
	}
}
