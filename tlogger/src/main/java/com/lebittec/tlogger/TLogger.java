/**
 * Feb 18, 2019
 */
package com.lebittec.tlogger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 * 
 * telegram logger api
 */
public class TLogger {

	private static final Logger logger = LoggerFactory.getLogger(TLogger.class);

	private static String token;

	private static Long defaultChatId;

	private static String configFileDir;

	private LoggerConfig config;

	/**
	 * Logger singleton instance
	 */
	private static TLogger tlogger;

	SimpleDateFormat sdf;

	/**
	 * Setup a telegram bot token and a default chat id
	 * @param token telegram bot token
	 * @param defaultChatId telegram group or user id
	 */
	public static void setup(String token, Long defaultChatId) {
		TLogger.token = token;
		TLogger.defaultChatId = defaultChatId;
	}
	
	/**
	 * Setup a telegram bot token a default chat id and a config file dir
	 * @param token telegram bot token
	 * @param defaultChatId telegram group or user id
	 * @param configDir directory of tlogger config file
	 */
	public static void setup(String token, Long defaultChatId, String configDir) {
		TLogger.token = token;
		TLogger.defaultChatId = defaultChatId;
		TLogger.configFileDir = configDir;
	}

	/**
	 * @return TLogger instance
	 */
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

	/*
	 * Disable all tlogger messages
	 */
	public void disable() {
		this.config.setActive(false);
	}

	/*
	 * Enable all tlogger messages
	 */
	public void enable() {
		this.config.setActive(true);
	}

	/**
	 * Reload tlogger configs from file
	 */
	public void reload() {
		this.config.loadConfig();
	}

	/**
	 * Send log to telegram through this params
	 * @param clazz any class
	 * @param msg A message to send
	 */
	public void send(Class<?> clazz, String msg) {
		this.send(Constants.DEFAULT_CHAT_NAME, clazz, msg, null);
	}

	/**
	 * Send log to telegram through this params
	 * @param chatName The name of config file's chat
	 * @param clazz any class
	 * @param msg A message to send
	 */
	public void send(String chatName, Class<?> clazz, String msg) {
		this.send(chatName, clazz, msg, null);
	}

	/**
	 * Send log to telegram through this params
	 * @param clazz any class
	 * @param msg A message to send
	 * @param t A Throwable to extract stacktrace
	 */
	public void send(Class<?> clazz, String msg, Throwable t) {
		this.send(Constants.DEFAULT_CHAT_NAME, clazz, msg, t);
	}

	/**
	 * Send log to telegram through this params
	 * @param clazz any class
	 * @param t A Throwable to extract stacktrace
	 */
	public void send(Class<?> clazz, Throwable t) {
		this.send(Constants.DEFAULT_CHAT_NAME, clazz, null, t);
	}

	/**
	 * Send log to telegram through this params
	 * @param chatName The name of config file's chat
	 * @param clazz any class
	 * @param t A Throwable to extract stacktrace
	 */
	public void send(String chatName, Class<?> clazz, Throwable t) {
		this.send(chatName, clazz, null, t);
	}

	/**
	 * Send log to telegram through this params
	 * @param chatName The name of config file's chat
	 * @param msg A message to send
	 * @param t A Throwable to extract stacktrace
	 */
	public void send(String chatName, String msg, Throwable t) {
		this.send(chatName, null, msg, t);
	}

	/**
	 * Send log to telegram through this params
	 * @param chatName The name of config file's chat
	 * @param msg A message to send
	 */
	public void send(String chatName, String msg) {
		this.send(chatName, null, msg, null);
	}

	/**
	 * Send log to telegram through this params
	 * @param msg A message to send
	 */
	public void send(String msg) {
		this.send(Constants.DEFAULT_CHAT_NAME, null, msg, null);
	}

	/**
	 * Send log to telegram through this params
	 * @param chatName The name of config file's chat
	 * @param clazz any class
	 * @param msg A message to send
	 * @param t A Throwable to extract stacktrace
	 */
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
			logger.error("Error on async tlogger execution ", e);
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
		content.append(' ');
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
