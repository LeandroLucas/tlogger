/**
 * 
 */
package com.lebittec.tlogger.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 *
 */
public final class Utils {

	public static List<String> divideMessageContent(String content, final int maxPerMessage) {
		List<String> msgs = null;
		if (content != null) {
			int start = 0;
			final int size = new BigDecimal(content.length()).divide(new BigDecimal(maxPerMessage), RoundingMode.UP)
					.intValue();
			int end = (maxPerMessage > content.length() ? content.length() - 1 : maxPerMessage);
			msgs = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				msgs.add(content.substring(start, end));
				start += maxPerMessage;
				end = (end + maxPerMessage > content.length() ? content.length() - 1 : end + maxPerMessage);
			}
		}
		return msgs;
	}

	public static String getStacktrace(Throwable t) throws IOException {
		if (t == null)
			return "";
		StringWriter stacktrace = new StringWriter();
		PrintWriter pw = new PrintWriter(stacktrace);
		t.printStackTrace(pw);
		pw.close();
		stacktrace.close();
		return stacktrace.toString();
	}

	private static File findFile(final String dir, String fileName) {
		File file = new File(dir + fileName);
		if (!file.exists()) {
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			file = new File(classLoader.getResource(fileName).getFile());
		}
		return file;
	}

	public static JsonObject loadFileToJsonObject(final String dir, final String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(findFile(dir, fileName)));
		JsonObject jsonObject = new JsonParser().parse(br).getAsJsonObject();
		br.close();
		return jsonObject;
	}

	public static String getHostName() {
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			hostName = "--";
		}
		return hostName;
	}
}
