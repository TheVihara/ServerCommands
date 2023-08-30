package net.astrona.proxy.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.*;

public class PluginMessageListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("server-commands:messaging")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                String input = in.readUTF();
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(channel);

                this.sendProxyMessage(serverInfo, input);
                ((ProxiedPlayer) event.getReceiver()).connect(serverInfo, ServerConnectEvent.Reason.KICK_REDIRECT);
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
