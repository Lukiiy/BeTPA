package me.lukiiy.beTPA.commands;

import me.lukiiy.beTPA.BeTPA;
import me.lukiiy.beTPA.Request;
import me.lukiiy.beTPA.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class Accept implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cThis command can only be used by in-game players.");
            return true;
        }

        if (!BeTPA.getInstance().quickPermissionCheck(commandSender, "answer")) {
            commandSender.sendMessage(BeTPA.getInstance().getConfiguredMsg("permission"));
            return true;
        }

        TPAManager tpaManager = BeTPA.getInstance().getTPAManager();
        Player player = (Player) commandSender;

        Collection<Request> requests = tpaManager.getPendingRequests(player);

        if (strings.length < 1) tpaManager.acceptAll(player);
        else {
            Player requester = Bukkit.getServer().getPlayer(strings[0]);

            if (requester == null) {
                commandSender.sendMessage(BeTPA.getInstance().getConfiguredMsg("notfound"));
                return true;
            }

            tpaManager.accept(player, requester.getUniqueId());
        }

        commandSender.sendMessage(requests.isEmpty() ? BeTPA.getInstance().getConfiguredMsg("nothing") : BeTPA.getInstance().getConfiguredMsg("accept").replace("%p", requests.stream().map(r -> r.requester.getDisplayName()).collect(Collectors.joining(", "))));
        requests.stream().map(r -> r.requester).forEach(sender -> sender.sendMessage(BeTPA.getInstance().getConfiguredMsg("acceptSender").replace("%p", player.getDisplayName())));
        return true;
    }
}
