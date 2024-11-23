package dev.khanh.plugin.vuongquoc.command.subcommand;

import dev.khanh.plugin.kplugin.command.BukkitCommand;
import dev.khanh.plugin.kplugin.util.ColorUtil;
import dev.khanh.plugin.kplugin.util.MessageUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.command.RealmAdminCommand;
import dev.khanh.plugin.vuongquoc.file.ConfigFile;
import dev.khanh.plugin.vuongquoc.realm.Realm;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class SetSpawnCommand extends BukkitCommand {
    private final RealmAdminCommand parent;
    private final RealmPlugin plugin;

    public SetSpawnCommand(RealmAdminCommand parent, RealmPlugin plugin) {
        super("setspawn", parent, null, "Thiệt lập tọa độ spawn của vương quốc",
                "/realmadmin setspawn &d<realm>");
        this.parent = parent;
        this.plugin = plugin;
    }
    @Override
    public void onCommand(CommandSender sender, List<String> args) {
        MessageUtil.sendMessageWithPrefix(sender, ChatColor.RED + "In-game command only!");
    }

    @Override
    public void onCommand(Player player, List<String> args) {
        if (args.size() == 1) {
            Realm realm = plugin.getRealmManager().getRealm(args.get(0)).orElse(null);
            if (realm == null) {
                MessageUtil.sendMessageWithPrefix(player, ChatColor.RED + "Không tìm thấy vương quốc: " + args.get(0));
                return;
            }

            setRealmSpawnLocation(realm, player.getLocation());

            MessageUtil.sendMessageWithPrefix(player,
                    ChatColor.GREEN + "Đã cập nhật thành công tọa độ của vương quốc " + args.get(0));
            return;
        }

        player.sendMessage(getUnknownCommandMessage());
    }

    private void setRealmSpawnLocation(Realm realm, Location location) {
        realm.setSpawnLocation(location);

        ConfigFile configFile = plugin.getConfigFile();
        configFile.set("realms." + realm.getName() + ".spawn-location", location);
        try {
            configFile.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNoPermissionMessage() {
        return parent.getNoPermissionMessage();
    }

    @Override
    public String getUnknownCommandMessage() {
        return ColorUtil.colorize(MessageUtil.getMessage("prefix") +
                MessageUtil.getMessage("command-syntax-error").replace("{usage}", getUsage()));
    }

    @Override
    public List<String> onTabComplete(Player player, List<String> args) {
        if (args.size() == 1) {
            return plugin.getRealmManager().getRealms()
                    .keySet()
                    .stream()
                    .filter(s -> s.startsWith(args.get(0)))
                    .toList();
        }
        return List.of();
    }
}
