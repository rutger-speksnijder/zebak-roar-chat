package com.zebakroarchat;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Zebak roar chat")
public interface ZebakRoarChatConfig extends Config
{
	@ConfigItem(
		keyName = "zebakMessage",
		name = "Zebak message",
		description = "Message to display when Zebak roars"
	)
	default String zebakMessage()
	{
		return "Rawr XD";
	}

	@ConfigItem(
			keyName = "soundFile",
			name = "Sound file",
			description = "Specify a path to a sound file you would like to be played when Zebak roars."
	)
	default String soundFile()
	{
		return "";
	}

	@ConfigItem(
			keyName = "volume",
			name = "Volume",
			description = "The sound's volume"
	)
	default int volume()
	{
		return 80;
	}
}
