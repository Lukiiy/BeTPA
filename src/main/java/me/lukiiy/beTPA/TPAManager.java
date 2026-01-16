package me.lukiiy.beTPA;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TPAManager {
    public long time;
    private final Map<UUID, Map<UUID, Request>> requests = new ConcurrentHashMap<>();

    public Request.Result sendRequest(Player requester, Player target) {
        if (requester.equals(target)) return Request.Result.SELF_REQUEST;

        requests.computeIfAbsent(target.getUniqueId(), k -> new ConcurrentHashMap<>());

        Map<UUID, Request> targetRequests = requests.get(target.getUniqueId());
        UUID requesterId = requester.getUniqueId();
        UUID targetId = target.getUniqueId();

        if (targetRequests.containsKey(requester.getUniqueId())) return Request.Result.ALREADY_REQUESTED;

        int taskId = -1;
        if (time > -1) {
            taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BeTPA.getInstance(), () -> {
                Map<UUID, Request> map = requests.get(targetId);
                if (map == null) return;

                map.remove(requesterId);
                if (map.isEmpty()) requests.remove(targetId);
            }, Math.max(20, 20 * time));
        }

        targetRequests.put(requesterId, new Request(requester, target, taskId));
        return Request.Result.SUCCESS;
    }

    public Collection<Request> getPendingRequests(Player target) {
        Map<UUID, Request> map = requests.get(target.getUniqueId());

        return map == null ? Collections.emptyList() : new ArrayList<>(map.values());
    }

    public void accept(Player target, UUID requesterId) {
        Map<UUID, Request> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests == null) return;

        Request request = targetRequests.remove(requesterId);
        if (request == null) return;

        request.cancelExpiry();

        Player requester = request.requester;
        if (requester != null && requester.isOnline()) requester.teleport(target.getLocation());

        if (targetRequests.isEmpty()) requests.remove(target.getUniqueId());
    }

    public void acceptAll(Player target) {
        Map<UUID, Request> targetRequests = requests.remove(target.getUniqueId());
        if (targetRequests == null) return;

        for (Request request : targetRequests.values()) {
            request.cancelExpiry();
            Player requester = request.requester;

            if (requester != null && requester.isOnline()) requester.teleport(target.getLocation());
        }
    }

    public void deny(Player target, UUID requesterId) {
        Map<UUID, Request> targetRequests = requests.get(target.getUniqueId());
        if (targetRequests == null) return;

        Request req = targetRequests.remove(requesterId);
        if (req == null) return;

        req.cancelExpiry();

        if (targetRequests.isEmpty()) requests.remove(target.getUniqueId());
    }

    public void denyAll(Player target) {
        Map<UUID, Request> targetRequests = requests.remove(target.getUniqueId());
        if (targetRequests == null) return;

        for (Request req : targetRequests.values()) req.cancelExpiry();
    }

    public void removeAllFor(Player player) {
        UUID id = player.getUniqueId();
        Map<UUID, Request> asTarget = requests.remove(id);

        if (asTarget != null) for (Request r : asTarget.values()) r.cancelExpiry();

        for (Map<UUID, Request> targetRequests : requests.values()) {
            Request removed = targetRequests.remove(id);

            if (removed != null) removed.cancelExpiry();
        }
    }
}
