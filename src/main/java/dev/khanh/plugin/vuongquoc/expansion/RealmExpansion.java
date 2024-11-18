package dev.khanh.plugin.vuongquoc.expansion;

import dev.khanh.plugin.kplugin.util.ColorUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.user.User;
import dev.khanh.plugin.vuongquoc.user.UserManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RealmExpansion extends PlaceholderExpansion {
    private final RealmPlugin plugin;

    public RealmExpansion(RealmPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vuongquoc";
    }

    @Override
    public @NotNull String getAuthor() {
        return "KhanhHuynh";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        UserManager userManager = plugin.getUserManager();
        User user = userManager.getUser(player.getName());

        switch (params.toLowerCase()) {
            case "realm_name" -> {
                if (user == null) {
                    return ChatColor.RED + "Offline";
                }
                return user.getRealm() == null ? "null" : user.getRealm().getName();
            }
            case "realm_display" -> {
                if (user == null) {
                    return ChatColor.RED + "Offline";
                }
                return user.getRealm() == null ? "null" : ColorUtil.colorize(user.getRealm().getDisplay());
            }
            case "realm_color" -> {
                if (user == null) {
                    return ChatColor.RED + "Offline";
                }
                return user.getRealm() == null ? "" : user.getRealm().getColor();
            }
        }

        return null;
    }
}
