package me.lukiiy.beTPA.commands;

import me.lukiiy.beTPA.BeTPA;
import me.lukiiy.beTPA.Request;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Send implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThis command can only be used by in-game players.");
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(BeTPA.getInstance().getConfiguredMsg("usage").replace("%c", "/tpa <player>"));
            return true;
        }

        Player player = (Player) commandSender;
        Player target = Bukkit.getServer().getPlayer(strings[0]);

        if (target == null || !target.isOnline()) {
            commandSender.sendMessage(BeTPA.getInstance().getConfiguredMsg("notfound"));
            return true;
        }

        Request.Result result = BeTPA.getInstance().getTPAManager().sendRequest(player, target);

        switch (result) {
            case SUCCESS:
                long seconds = BeTPA.getInstance().getTPAManager().time;

                player.sendMessage(BeTPA.getInstance().getConfiguredMsg("send").replace("%p", target.getDisplayName()).replace("%s", seconds + ""));
                target.sendMessage(BeTPA.getInstance().getConfiguredMsg("receive").replace("%p", player.getDisplayName()).replace("%s", seconds + ""));
                break;
            case SELF_REQUEST:
                player.sendMessage(BeTPA.getInstance().getConfiguredMsg("self"));
                break;
            case ALREADY_REQUESTED:
                player.sendMessage(BeTPA.getInstance().getConfiguredMsg("already").replace("%p", target.getDisplayName()));
                break;
        }

        return true;
    }
}
