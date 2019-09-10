/**
 * 
 */
package com.lebittec.tlogger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 *
 */
class HttpConnection {

	private HttpURLConnection connection;

	protected HttpConnection(String url) throws IOException {
		connection = this.openConnection(url);
	}

	protected HttpURLConnection openConnection(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		return con;
	}

	protected void addParameters(String urlParameters) throws IOException {
		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.write(urlParameters.getBytes("UTF-8"));
		wr.flush();
		wr.close();
	}

	protected Object execute() throws IOException {
		return connection.getContent();
	}
}
