package net.astrona.bukkit;

import net.astrona.bukkit.commands.ServerCommand;
import net.astrona.bukkit.handlers.PluginMessageHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "server-commands:messaging");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "server-commands:messaging", new PluginMessageHandler());
        this.getServer().getPluginCommand("server").setExecutor(new ServerCommand(this));
    }
}