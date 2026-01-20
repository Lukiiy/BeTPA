package me.lukiiy.beTPA;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TPAManager {
    public long time;
    private final Map<Player, Map<Player, Request>> requests = new ConcurrentHashMap<>();

    public Request.Result sendRequest(Player requester, Player target) {
        if (requester.equals(target)) return Request.Result.SELF_REQUEST;

        requests.computeIfAbsent(target, k -> new ConcurrentHashMap<>());

        Map<Player, Request> targetReq = requests.get(target);

        if (targetReq.containsKey(requester)) return Request.Result.ALREADY_REQUESTED;

        int taskId = -1;
        if (time > -1) {
            taskId = BeTPA.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BeTPA.getInstance(), () -> {
                Map<Player, Request> map = requests.get(target);
                if (map == null) return;

                requester.sendMessage(BeTPA.getInstance().getConfiguredMsg("ignoreSender").replace("%p", target.getDisplayName()));
                target.sendMessage(BeTPA.getInstance().getConfiguredMsg("ignore").replace("%p", requester.getDisplayName()));

                map.remove(requester);
                if (map.isEmpty()) requests.remove(target);
            }, Math.max(20, 20 * time));
        }

        targetReq.put(requester, new Request(requester, target, taskId));
        return Request.Result.SUCCESS;
    }

    public Collection<Request> getPendingRequests(Player target) {
        Map<Player, Request> map = requests.get(target);

        return map == null ? Collections.emptyList() : new ArrayList<>(map.values());
    }

    public void accept(Player target, Player requester) {
        Map<Player, Request> targetReq = requests.get(target);
        if (targetReq == null) return;

        Request request = targetReq.remove(requester);
        if (request == null) return;

        request.cancelExpiry();

        Player requester2 = request.requester;
        if (requester2 != null) requester2.teleportTo(target.getLocation());

        if (targetReq.isEmpty()) requests.remove(target);
    }

    public void acceptAll(Player target) {
        Map<Player, Request> targetReq = requests.remove(target);
        if (targetReq == null) return;

        for (Request request : targetReq.values()) {
            request.cancelExpiry();
            Player requester = request.requester;

            if (requester != null && requester.isOnline()) requester.teleportTo(target.getLocation());
        }
    }

    public void deny(Player target, Player requester) {
        Map<Player, Request> targetReq = requests.get(target);
        if (targetReq == null) return;

        Request req = targetReq.remove(requester);
        if (req == null) return;

        req.cancelExpiry();

        if (targetReq.isEmpty()) requests.remove(target);
    }

    public void denyAll(Player target) {
        Map<Player, Request> targetReq = requests.remove(target);
        if (targetReq == null) return;

        for (Request req : targetReq.values()) req.cancelExpiry();
    }

    public void removeAllFor(Player player) {
        Map<Player, Request> asTarget = requests.remove(player);
        if (asTarget != null) for (Request r : asTarget.values()) r.cancelExpiry();

        for (Map<Player, Request> targetRequests : requests.values()) {
            Request removed = targetRequests.remove(player);

            if (removed != null) removed.cancelExpiry();
        }
    }
}
