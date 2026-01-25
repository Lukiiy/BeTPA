package me.lukiiy.beTPA;

import me.lukiiy.beTPA.commands.Accept;
import me.lukiiy.beTPA.commands.Deny;
import me.lukiiy.beTPA.commands.Reload;
import me.lukiiy.beTPA.commands.Send;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BeTPA extends JavaPlugin {
    private static BeTPA instance;
    private TPAManager tpaManager;

    @Override
    public void onEnable() {
        instance = this;
        setupConfig();

        tpaManager = new TPAManager();
        tpaManager.time = getConfiguration().getInt("timeout", 60);

        getCommand("tpa").setExecutor(new Send());
        getCommand("tpaccept").setExecutor(new Accept());
        getCommand("tpadeny").setExecutor(new Deny());
        getCommand("tpareload").setExecutor(new Reload());
    }

    @Override
    public void onDisable() { }

    public static BeTPA getInstance() {
        return instance;
    }

    public TPAManager getTPAManager() {
        return tpaManager;
    }

    // Config
    public void setupConfig() {
        getConfiguration().load();

        configSetIfNull("msgs.usage", "§cUsage: %c");
        configSetIfNull("msgs.notfound", "§cNo player was found.");
        configSetIfNull("msgs.send", "You sent a teleport request to §e%p§f. They have %s seconds to reply.");
        configSetIfNull("msgs.receive", "§e%p §fsent you a teleport request. You have %s seconds to §a/tpaccept§f or §c/tpadeny§f.");
        configSetIfNull("msgs.accept", "You §aaccepted§f the teleport request from §e%p§f.");
        configSetIfNull("msgs.acceptSender", "§e%p §aaccecpted§f your teleport request.");
        configSetIfNull("msgs.deny", "You §cdenied§f the teleport request from §e%p§f.");
        configSetIfNull("msgs.denySender", "§e%p §cdenied§f your teleport request.");
        configSetIfNull("msgs.self", "§cYou cannot send a request to yourself.");
        configSetIfNull("msgs.already", "§cYou already sent a request to %p.");
        configSetIfNull("msgs.ignore", "§cYou ignored %p's teleport request.");
        configSetIfNull("msgs.ignoreSender", "§c%p has ignored your teleport request.");
        configSetIfNull("msgs.nothing", "§cThere are no requests to accept.");
        configSetIfNull("timeout", 60);

        getConfiguration().save();
    }

    private void configSetIfNull(String key, Object value) {
        if (getConfiguration().getProperty(key) == null) getConfiguration().setProperty(key, value);
    }

    public String getConfiguredMsg(String key) {
        return getConfiguration().getString("msgs." + key, "").replace('&', '§');
    }
}
