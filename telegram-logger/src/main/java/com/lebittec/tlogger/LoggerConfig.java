/**
 * 
 */
package com.lebittec.tlogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author <a href="mailto:leandro.lucas_@hotmail.com">Leandro Lucas Santos</a>
 *
 */
class LoggerConfig {

	private String botToken;

	private Long defaultChatId;

	private String description;

	private boolean active;

	private Map<String, Long> chatMap = new HashMap<String, Long>();;

	private String configFileDir;

	protected LoggerConfig(String botToken, Long defaultChatId, String configFileDir) {
		Objects.requireNonNull(botToken, "Use TLogger.setup for setup botToken");
		Objects.requireNonNull(defaultChatId, "Use TLogger.setup for setup defaultChatId");
		this.botToken = botToken;
		this.defaultChatId = defaultChatId;
		this.configFileDir = configFileDir;
		this.loadConfig();
	}

	protected Long getChatId(String chatName) {
		Long chatId = this.chatMap.get(chatName);
		if (chatId == null) {
			chatId = this.chatMap.get(Constants.DEFAULT_CHAT_NAME);
		}
		return chatId;
	}

	protected void loadChats(JsonObject configObject) {
		this.chatMap.clear();
		JsonArray chats = configObject.get("chats").getAsJsonArray();
		for (int i = 0; i < chats.size(); i++) {
			JsonObject chat = chats.get(i).getAsJsonObject();
			this.chatMap.put(chat.get("name").getAsString(), chat.get("id").getAsLong());
		}
	}

	protected void loadConfig() {
		try {
			JsonObject configObject = Utils.loadFileToJsonObject(this.configFileDir == null ? Constants.DEFAULT_SCRIPTS_FOLDER : this.configFileDir,
					Constants.CONFIG_OBJECT_NAME);
			this.description = Utils.getHostName() + " | " + configObject.get("description").getAsString();
			this.active = configObject.get("active").getAsBoolean();
			this.loadChats(configObject);
		} catch (Exception e) {
			this.description = Utils.getHostName();
			this.active = true;
		}
	}

	/**
	 * @return the botToken
	 */
	public String getBotToken() {
		return botToken;
	}

	/**
	 * @param botToken the botToken to set
	 */
	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}

	/**
	 * @return the defaultChatId
	 */
	public Long getDefaultChatId() {
		return defaultChatId;
	}

	/**
	 * @param defaultChatId the defaultChatId to set
	 */
	public void setDefaultChatId(Long defaultChatId) {
		this.defaultChatId = defaultChatId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
