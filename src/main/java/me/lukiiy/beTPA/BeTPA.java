package me.lukiiy.beTPA;

import me.lukiiy.beTPA.commands.Accept;
import me.lukiiy.beTPA.commands.Deny;
import me.lukiiy.beTPA.commands.Reload;
import me.lukiiy.beTPA.commands.Send;
import org.bukkit.plugin.java.JavaPlugin;

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

        getConfiguration().getString("msgs.usage", "§cUsage: %c");
        getConfiguration().getString("msgs.notfound", "§cNo player was found.");
        getConfiguration().getString("msgs.send", "You sent a teleport request to §e%p§f. They have %s seconds to reply.");
        getConfiguration().getString("msgs.receive", "§e%p §fsent you a teleport request. You have %s seconds to §a/tpaccept§f or §c/tpadeny§f.");
        getConfiguration().getString("msgs.accept", "You §aaccepted§f the teleport request from §e%p§f.");
        getConfiguration().getString("msgs.acceptSender", "§e%p §aaccecpted§f your teleport request.");
        getConfiguration().getString("msgs.deny", "You §cdenied§f the teleport request from §e%p§f.");
        getConfiguration().getString("msgs.denySender", "§e%p §cdenied§f your teleport request.");
        getConfiguration().getString("msgs.self", "§cYou cannot send a request to yourself.");
        getConfiguration().getString("msgs.already", "§cYou already sent a request to %p.");
        getConfiguration().getString("msgs.ignored", "§c%p has ignored your teleport request.");
        getConfiguration().getString("msgs.nothing", "§cThere are no requests to accept.");
        getConfiguration().getInt("timeout", 60);

        getConfiguration().save();
    }

    public String getConfiguredMsg(String key) {
        return getConfiguration().getString("msgs." + key, "").replace('&', '§');
    }
}
