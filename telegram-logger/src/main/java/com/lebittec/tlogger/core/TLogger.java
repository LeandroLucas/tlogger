/**
 * Feb 18, 2019
 */
package com.lebittec.tlogger.core;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lebittec.tlogger.models.HttpConnection;
import com.lebittec.tlogger.models.LoggerConfig;
import com.lebittec.tlogger.utils.Constants;
import com.lebittec.tlogger.utils.Utils;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 */
public class TLogger {

	private static final Logger logger = LoggerFactory.getLogger(TLogger.class);

	private static String token;

	private static Long defaultChatId;

	private static String configFileDir;

	private LoggerConfig config;

	/**
	 * Logger instance
	 */
	private static TLogger tlogger;

	SimpleDateFormat sdf;

	public static void setup(String token, Long defaultChatId) {
		TLogger.token = token;
		TLogger.defaultChatId = defaultChatId;
	}

	public static TLogger getLogger() {
		if (tlogger == null) {
			tlogger = new TLogger();
		}
		return tlogger;
	}

	private TLogger() {
		this.sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		this.config = new LoggerConfig(TLogger.token, TLogger.defaultChatId, TLogger.configFileDir);
	}

	public void disable() {
		this.config.setActive(false);
	}

	public void enable() {
		this.config.setActive(true);
	}

	public void reload() {
		this.config.loadConfig();
	}

	public void send(Class<?> clazz, String msg) {
		this.send(Constants.DEFAULT_CHAT_NAME, clazz, msg, null);
	}

	public void send(String chatName, Class<?> clazz, String msg) {
		this.send(chatName, clazz, msg, null);
	}

	public void send(Class<?> clazz, String msg, Throwable t) {
		this.send(Constants.DEFAULT_CHAT_NAME, clazz, msg, t);
	}

	public void send(Class<?> clazz, Throwable t) {
		this.send(Constants.DEFAULT_CHAT_NAME, clazz, null, t);
	}

	public void send(String chatName, Class<?> clazz, Throwable t) {
		this.send(chatName, clazz, null, t);
	}

	public void send(String chatName, String msg, Throwable t) {
		this.send(chatName, null, msg, t);
	}

	public void send(String chatName, String msg) {
		this.send(chatName, null, msg, null);
	}

	public void send(String msg) {
		this.send(Constants.DEFAULT_CHAT_NAME, null, msg, null);
	}

	public void send(String chatName, Class<?> clazz, String msg, Throwable t) {
		this.sendAsync(this.config.getChatId(chatName), clazz, msg, t);
	}

	private void sendAsync(Long chatId, Class<?> clazz, String msg, Throwable t) {
		try {
			if (!this.config.isActive()) {
				return;
			}
			new Thread(() -> sendMsg(chatId, clazz, msg, t)).start();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private synchronized void sendMsg(Long chatId, Class<?> clazz, String msg, Throwable t) {
		try {
			if (chatId == null) {
				chatId = config.getDefaultChatId();
			}
			List<String> contents = this.buildContents(clazz, msg, t);
			for (String content : contents) {
				HttpConnection con = new HttpConnection(buildUrl(this.config.getBotToken()));
				con.addParameters(buildParameters(chatId, content));
				con.execute();
			}
		} catch (Throwable e) {
			logger.error("", e);
		}
	}

	private List<String> buildContents(Class<?> clazz, String msg, Throwable t) throws IOException {
		StringBuilder content = new StringBuilder();
		content.append('[');
		content.append(this.config.getDescription());
		content.append(']');
		if (Objects.nonNull(clazz)) {
			content.append(clazz.getName());
		}
		content.append(' ');
		content.append(this.sdf.format(new Date()));
		if (msg != null) {
			content.append(":\n");
			content.append(msg);
		}
		content.append('\n');
		content.append(Utils.getStacktrace(t));
		return Utils.divideMessageContent(content.toString(), 4096);
	}

	private String buildUrl(String token) {
		StringBuilder url = new StringBuilder();
		url.append(Constants.TELEGRAM_API_URL);
		url.append(token);
		url.append(Constants.SEND_MESSAGE_ENDPOINT);
		return url.toString();
	}

	private String buildParameters(Long idChannel, String content) {
		StringBuilder urlParameters = new StringBuilder();
		urlParameters.append("chat_id=");
		urlParameters.append(idChannel);
		urlParameters.append("&text=");
		urlParameters.append(content);
		return urlParameters.toString();
	}

}
