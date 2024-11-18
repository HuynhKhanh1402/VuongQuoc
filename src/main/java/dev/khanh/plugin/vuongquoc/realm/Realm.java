package dev.khanh.plugin.vuongquoc.realm;

import dev.khanh.plugin.kplugin.util.ColorUtil;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@Getter
public class Realm {

    private final String name;
    private final String display;
    private final String color;
    @Setter
    private Location spawnLocation;

    public Realm(String name, String display, String color, @Nullable Location spawnLocation) {
        this.name = name;
        this.display = ColorUtil.colorize(PlaceholderAPI.setBracketPlaceholders(null, display));
        this.color = color;
        this.spawnLocation = spawnLocation;
    }
}
