package com.zebakroarchat;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ZebakRoarChatPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ZebakRoarChatPlugin.class);
		RuneLite.main(args);
	}
}