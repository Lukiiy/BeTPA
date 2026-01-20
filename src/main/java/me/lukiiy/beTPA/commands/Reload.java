package me.lukiiy.beTPA.commands;

import me.lukiiy.beTPA.BeTPA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Reload {
    public static void onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        BeTPA.getInstance().getConfiguration().load();
        BeTPA.getInstance().getTPAManager().time = BeTPA.getInstance().getConfiguration().getInt("timeout", 60);

        commandSender.sendMessage("§aBeTPA Reload complete!");
    }
}
