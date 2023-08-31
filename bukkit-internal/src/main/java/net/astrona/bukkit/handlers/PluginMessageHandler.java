package net.astrona.bukkit.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessageHandler implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("server-commands:messaging")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String subChannel = in.readUTF();
            String[] splitSubChannel = subChannel.split("::");

            switch (splitSubChannel[0]) {
                case "player":
                    Player sender = Bukkit.getPlayer(splitSubChannel[1]);
                    sender.performCommand(splitSubChannel[2]);
                    break;
                case "console":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), splitSubChannel[2]);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
