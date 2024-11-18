package dev.khanh.plugin.vuongquoc.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;
import java.util.stream.Collectors;

public class WGUtils {

    public static boolean regionExists(World world, String regionName) {
        WorldGuardPlatform worldGuardPlatform = WorldGuard.getInstance().getPlatform();

        RegionContainer rgContainer = worldGuardPlatform.getRegionContainer();
        RegionManager regionManager = rgContainer.get(BukkitAdapter.adapt(world));

        assert regionManager != null;
        return regionManager.getRegion(regionName) != null;
    }

    public static boolean regionExists(String regionName) {
        for (World world: Bukkit.getWorlds()) {
            if (regionExists(world, regionName)) {
                return true;
            }
        }
        return false;
    }

    public static Set<ProtectedRegion> getRegions(Location location) {
        WorldGuardPlatform worldGuardPlatform = WorldGuard.getInstance().getPlatform();

        RegionContainer rgContainer = worldGuardPlatform.getRegionContainer();
        RegionQuery buildQuery = rgContainer.createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);

        ApplicableRegionSet set = buildQuery.getApplicableRegions(loc);

        return (set.getRegions());
    }

    public static Set<String> getRegionNames(Location location) {
        return getRegions(location)
                .stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toSet());
    }
}