package dev.khanh.plugin.vuongquoc.command;

import dev.khanh.plugin.kplugin.command.BukkitCommand;
import dev.khanh.plugin.kplugin.instance.InstanceManager;
import dev.khanh.plugin.kplugin.util.ColorUtil;
import dev.khanh.plugin.kplugin.util.MessageUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.file.ConfigFile;
import dev.khanh.plugin.vuongquoc.user.User;
import dev.khanh.plugin.vuongquoc.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends BukkitCommand {
    private final RealmPlugin plugin;

    public SpawnCommand(RealmPlugin plugin) {
        super("spawn", null, List.of(), "vuongquoc.spawn", null, "/spawn");
        this.plugin = plugin;

        registerCommand(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, List<String> args) {
        MessageUtil.sendMessageWithPrefix(sender, ChatColor.RED + "In-game command only!");
    }

    @Override
    public void onCommand(Player player, List<String> args) {
        UserManager userManager = plugin.getUserManager();
        ConfigFile configFile = plugin.getConfigFile();

        User user = userManager.getUser(player);

        if (user.getRealm() == null || user.getRealm().getSpawnLocation() == null) {
            Bukkit.dispatchCommand(player, configFile.getSpawnFallbackCommand());
        } else {
            player.teleport(user.getRealm().getSpawnLocation());
            MessageUtil.sendMessage(player, "spawn-message", s -> s.replace("{realm}", user.getRealm().getDisplay()));
        }
    }

    @Override
    public String getNoPermissionMessage() {
        return MessageUtil.getColorizedMessage("prefix") + MessageUtil.getColorizedMessage("no-perm");
    }

    @Override
    public String getUnknownCommandMessage() {
        return ColorUtil.colorize(MessageUtil.getMessage("prefix") +
                MessageUtil.getMessage("command-syntax-error").replace("{usage}", getUsage()));
    }
}
