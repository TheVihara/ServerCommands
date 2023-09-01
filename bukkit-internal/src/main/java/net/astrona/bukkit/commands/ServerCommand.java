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
            sender.sendMessage(ChatColor.DARK_RED + "Usage: /server <server> <-p|-c> <command>");
            return false;
        }

        String server = args[0];
        String senderType = args[1].equalsIgnoreCase("-p") ? "player" : args[1].equalsIgnoreCase("-c") ? "console" : "none";
        String cmd = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (senderType.equalsIgnoreCase("console")) {
            if (!sender.hasPermission("servercommands.use.console")) {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use the console sender type.");
                return false;
            }
        }

        if (server == null || senderType.equalsIgnoreCase("none") || cmd == null || server.isEmpty() || cmd.isEmpty()) {
            sender.sendMessage(ChatColor.DARK_RED + "The specified server or the specified command & sender either doesn't exist or was not provided.");
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

        sendProxyMessage(player, "server-commands:messaging", server, senderType + "::" + cmd);
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
