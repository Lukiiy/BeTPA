package me.lukiiy.beTPA;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Request { // record :(
    public final Player requester;
    public final Player target;
    private final int expiryTask;

    public Request(Player requester, Player target, int expiryTask) {
        this.requester = requester;
        this.target = target;
        this.expiryTask = expiryTask;
    }

    public void cancelExpiry() {
        if (expiryTask < 0) return;

        Bukkit.getServer().getScheduler().cancelTask(expiryTask);
    }

    public enum Result {
        SUCCESS,
        SELF_REQUEST,
        ALREADY_REQUESTED
    }
}
