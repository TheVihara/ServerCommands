package net.astrona.proxy;

import net.astrona.proxy.listeners.PluginMessageListener;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPlugin extends Plugin {
    @Override
    public void onEnable() {
        getProxy().registerChannel("server-commands:messaging");
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
    }
}