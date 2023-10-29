package com.zebakroarchat;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

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

	private Clip clip;

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		if (npcDespawned.getNpc().getId() == NpcID.ZEBAK_11730)
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

		if (npc.getId() != NpcID.ZEBAK_11730 || npc.getAnimation() != 9628)
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
		playSound();

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

	private void playSound()
	{
		String soundFile = config.soundFile();
		if (soundFile.length() == 0) {
			return;
		}

		if (clip != null) {
			clip.close();
		}

		AudioInputStream inputStream = null;
		try {
			URL url = Paths.get(soundFile).toUri().toURL();
			inputStream = AudioSystem.getAudioInputStream(url);
		} catch (UnsupportedAudioFileException | IOException e) {
			log.warn("Could not create input stream: ", e);
		}

		if (inputStream == null) {
			return;
		}

		try
		{
			clip = AudioSystem.getClip();
			clip.open(inputStream);
		} catch (LineUnavailableException | IOException e) {
			log.warn("Could not load sound file: ", e);
		}

		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float volumeValue = config.volume() - 100;

		volume.setValue(volumeValue);
		clip.loop(0);
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed)
	{
		int soundId = soundEffectPlayed.getSoundId();
		if (soundId == 5881) {
			soundEffectPlayed.consume();
		}
	}
}
