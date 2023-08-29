package net.astrona.bukkit.commands;

import net.astrona.bukkit.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ServerCommand implements TabExecutor {
    private BukkitPlugin plugin;

    public ServerCommand(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.DARK_RED + "Usage: /server <server> <command>");
            return false;
        }

        String server = args[0];
        String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (server == null || cmd == null || server.isEmpty() || cmd.isEmpty()) {
            sender.sendMessage(ChatColor.DARK_RED + "The specified server or the specified command either doesn't exist or was not provided.");
            return false;
        }

        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "There are no players online.");
                return true;
            }
            player = onlinePlayers.iterator().next();
        }

        sendProxyMessage(player, "server-commands:messaging", server, cmd);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private void sendProxyMessage(Player player, String channel, String server, String cmd) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(server);
            out.writeUTF(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, channel, b.toByteArray());
    }
}
