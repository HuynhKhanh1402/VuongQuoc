package dev.khanh.plugin.vuongquoc.listener;

import dev.khanh.plugin.kplugin.util.LoggerUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.file.ConfigFile;
import dev.khanh.plugin.vuongquoc.user.User;
import dev.khanh.plugin.vuongquoc.user.UserManager;
import dev.khanh.plugin.vuongquoc.util.WGUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.Set;

public class PlayerListener implements Listener {
    private final RealmPlugin plugin;

    private final Set<String> respawnWaitingPlayers = new HashSet<>();

    public PlayerListener(RealmPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UserManager userManager = plugin.getUserManager();
        Player player = event.getPlayer();

        userManager.loadUser(player).thenRun(() -> LoggerUtil.info("Loaded %s's data".formatted(player.getName())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UserManager userManager = plugin.getUserManager();
        Player player = event.getPlayer();

        userManager.removeUser(player.getName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        respawnWaitingPlayers.add(event.getEntity().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        UserManager userManager = plugin.getUserManager();

        Player player = event.getPlayer();

        if (!respawnWaitingPlayers.contains(player.getName())) {
            return;
        }

        respawnWaitingPlayers.remove(player.getName());

        User user = userManager.getUser(player);

        if (user.getRealm() != null && user.getRealm().getSpawnLocation() != null) {
            event.setRespawnLocation(user.getRealm().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPVP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Player damager) {

            if (!canPVP(damager, damaged)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean canPVP(Player damager, Player damaged) {
        if (damager.hasPermission("vuongquoc.bypass.pvp")) {
            return true;
        }

        ConfigFile configFile = plugin.getConfigFile();
        UserManager userManager = plugin.getUserManager();

        if (configFile.isAllowRealmMemberPvp()) {
            return true;
        }

        User user1 = userManager.getUser(damager);
        User user2 = userManager.getUser(damaged);

        if (user1.getRealm() != null && user1.getRealm().equals(user2.getRealm())) {
            return isInCanPVPRegion(damager) && isInCanPVPRegion(damaged);
        }

        return true;
    }

    private boolean isInCanPVPRegion(Player player) {
        ConfigFile configFile = plugin.getConfigFile();

        for (String region: WGUtils.getRegionNames(player.getLocation())) {
            if (configFile.getAllowRealmMemberPvPRegions().contains(region)) {
                return true;
            }
        }

        return false;
    }
}
