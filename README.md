<h1 align="center">TLogger</h1>

<p align="center">
<a href="https://github.com/LeandroLucas/telegram-logger/blob/master/LICENSE" target="_blank">
<img src="https://img.shields.io/github/license/mashape/apistatus.svg">
</a>
</p>
<p align="center">
Java library to send log to telegram groups and users through bots using telegram http api
</p>

## How it works
<p>
  TLogger sends messages to selecioned users and groups in telegram through bots. Every message contains a timestamp, the hostname where the application is running and optionals informations like description, message, class name and stacktrace.
</p>

## How to use

### Coding:

Setup TLogger with a telegram bot token and a default chat id
```
TLogger.setup("BOT_TOKEN", -12314124);
```
Send logs to telegram
```
TLogger.getLogger().send(ExampleClass.class, "Message to send", new RuntimeException("Throwable example"));
```

### Setup tlogger.json

<p>
 tlogger.json is a file to setup chats and application description:
</p>

```
{
	"active": true,
	"description": "app description",
	"chats": [
		{
			"name": "group-x",
			"id": -0
		},
		{
			"name": "user-z",
			"id": 0
		}
	]
}
```

***active***: Disable the logger. if you need the logger to start off

***description***: A description of the application that will send logs

***chats***: Chat's to send logs. Chat's in tlogger.json are used by their names in the send method.
