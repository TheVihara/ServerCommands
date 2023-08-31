package net.astrona.proxy.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.astrona.proxy.ProxyPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class PluginMessageListener implements Listener {
    private ProxyPlugin plugin;
    private ProxyServer proxyServer;
    public PluginMessageListener(ProxyPlugin plugin, ProxyServer proxyServer) {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("server-commands:messaging")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                String input = in.readUTF();
                String[] splitInput = input.split("::");
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(channel);
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getReceiver();
                if (serverInfo != null) {
                    proxiedPlayer.connect(serverInfo, ServerConnectEvent.Reason.KICK_REDIRECT);

                    proxyServer.getScheduler().schedule(plugin, () -> {
                        this.sendProxyMessage(serverInfo, splitInput[0] + "::" + proxiedPlayer.getName() + "::" + splitInput[1]);
                    }, 500, TimeUnit.MILLISECONDS);
                } else {
                    proxiedPlayer.sendMessage(ChatColor.DARK_RED + "That server does not exist.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendProxyMessage(ServerInfo server, String cmd) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(cmd);

        server.sendData("server-commands:messaging", out.toByteArray());
    }
}
