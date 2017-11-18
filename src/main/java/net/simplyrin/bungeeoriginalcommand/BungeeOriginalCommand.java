package net.simplyrin.bungeeoriginalcommand;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class BungeeOriginalCommand extends Plugin implements Listener {

	private static BungeeOriginalCommand plugin;

	@Override
	public void onEnable() {
		plugin = this;

		plugin.getProxy().getPluginManager().registerListener(this, this);
		plugin.registerConfig();
	}

	private void registerConfig() {
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		File file = new File(plugin.getDataFolder(), "config.yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Configuration config = null;
			try {
				config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			config.set("Commands./test.Enable", true);
			config.set("Commands./test.Command", Arrays.asList("/say test", "/helpop test!"));

			config.set("Commands./test.Return-Message", Arrays.asList("&bHello!", "&bThis is test command!"));
			config.set("Commands./test.SendServer", "Lobby");

			try {
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		ProxiedPlayer player = (ProxiedPlayer)event.getSender();
		String[] args = event.getMessage().split(" ");

		File file = new File(plugin.getDataFolder(), "config.yml");
		Configuration config = null;
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(!event.isCommand()) {
			return;
		}

		if(config.getBoolean("Commands." + args[0] + ".Enable")) {
			event.setCancelled(true);

			if (config.getStringList("Commands." + args[0] + ".Command") != null) {
				for (String cmd : config.getStringList("Commands." + args[0] + ".Command")) {
					player.chat("/" + cmd);
				}
			}

			if (config.getStringList("Commands." + args[0] + ".Return-Message") != null) {
				for (String msg : config.getStringList("Commands." + args[0] + ".Return-Message")) {
					player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));
				}
			}

			if (config.getString("Commands." + args[0] + ".SendServer") != null) {
				player.connect(plugin.getProxy().getServerInfo(config.getString("Commands." + args[0] + ".SendServer")));
			}
		}
	}

}
