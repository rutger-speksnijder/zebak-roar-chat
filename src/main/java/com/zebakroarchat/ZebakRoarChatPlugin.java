package com.zebakroarchat;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
@PluginDescriptor(
	name = "Zebak Roar Chat"
)
public class ZebakRoarChatPlugin extends Plugin implements ActionListener {
	@Inject
	private Client client;

	@Inject
	private ZebakRoarChatConfig config;

	private boolean isRoaring = false;
	private NPC zebak = null;

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		if (npcDespawned.getNpc().getId() == NpcID.ZEBAK)
		{
			if (isRoaring)
			{
				endRoar();
			}

			if (zebak != null)
			{
				zebak = null;
			}
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (isRoaring)
		{
			return;
		}

		if (!(event.getActor() instanceof NPC))
		{
			return;
		}

		final NPC npc = (NPC) event.getActor();
		if (npc.getId() != NpcID.ZEBAK || npc.getAnimation() != 9628)
		{
			return;
		}

		zebak = npc;
		startRoar();
	}

	protected void startRoar()
	{
		isRoaring = true;
		zebak.setOverheadText(config.zebakMessage());

		Timer timer = new Timer(5500, this);
		timer.setRepeats(false);
		timer.start();
	}

	protected void endRoar()
	{
		isRoaring = false;
		zebak.setOverheadText("");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		endRoar();
	}

	@Provides
	ZebakRoarChatConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZebakRoarChatConfig.class);
	}
}
