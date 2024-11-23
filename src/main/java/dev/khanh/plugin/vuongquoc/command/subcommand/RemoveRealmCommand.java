package dev.khanh.plugin.vuongquoc.command.subcommand;

import dev.khanh.plugin.kplugin.command.BukkitCommand;
import dev.khanh.plugin.kplugin.util.ColorUtil;
import dev.khanh.plugin.kplugin.util.MessageUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.command.RealmAdminCommand;
import dev.khanh.plugin.vuongquoc.realm.Realm;
import dev.khanh.plugin.vuongquoc.user.User;
import dev.khanh.plugin.vuongquoc.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Stream;

public class RemoveRealmCommand extends BukkitCommand {
    private final RealmAdminCommand parent;
    private final RealmPlugin plugin;

    public RemoveRealmCommand(RealmAdminCommand parent, RealmPlugin plugin) {
        super("removerealm", parent, null, "Xóa người chơi khỏi vương quốc",
                "/realmadmin removerealm &d<player>");
        this.parent = parent;
        this.plugin = plugin;
    }
    @Override
    public void onCommand(CommandSender sender, List<String> args) {
        UserManager userManager = plugin.getUserManager();

        if (args.size() != 1) {
            sender.sendMessage(getUnknownCommandMessage());
            return;
        }

        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null) {
            MessageUtil.sendMessageWithPrefix(sender,
                    ChatColor.RED + "Người chơi %s không online.".formatted(args.get(0)));
            return;
        }
        User user = userManager.getUser(player);
        String playerName = args.get(0);

        if (user.getRealm() == null) {
            MessageUtil.sendMessageWithPrefix(sender,
                    ChatColor.YELLOW + "Người chơi %s hiện không ở bất kỳ vương quốc nào.".formatted(playerName));
            return;
        }
        Realm realm = user.getRealm();

        user.setRealm(null).thenRun(() -> {
            MessageUtil.sendMessageWithPrefix(sender,
                    ChatColor.GREEN + "Người chơi %s đã bị xoá khỏi vương %s".formatted(playerName, realm.getDisplay()));
            MessageUtil.sendMessage(player, "removed-from-realm",
                    s -> s.replace("{realm}", realm.getDisplay())
                            .replace("{commander}", sender instanceof Player ? sender.getName() : "CONSOLE")
            );
        }).exceptionally(throwable -> {
            throwable.printStackTrace();

            sender.sendMessage(ChatColor.RED + "An error occurred while execute command.");
            sender.sendMessage(ChatColor.RED + String.format("%s: %s", throwable.getClass().getName(), throwable.getMessage()));

            throw new RuntimeException();
        });
    }

    @Override
    public void onCommand(Player player, List<String> args) {
        onCommand((CommandSender) player, args);
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
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        switch (args.size()) {
            case 1 -> {
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(HumanEntity::getName)
                        .filter(s -> s.startsWith(args.get(0)))
                        .toList();
            }
            case 2 -> {
                return plugin.getRealmManager().getRealms()
                        .keySet()
                        .stream()
                        .filter(s -> s.startsWith(args.get(1)))
                        .toList();
            }
            case 3 -> {
                return Stream.of("true", "false")
                        .filter(s -> s.startsWith(args.get(2)))
                        .toList();
            }
            case 4 -> {
                return Stream.of("true", "false")
                        .filter(s -> s.startsWith(args.get(3)))
                        .toList();
            }

        }
        return List.of();
    }

    @Override
    public List<String> onTabComplete(Player player, List<String> args) {
        return onTabComplete((CommandSender) player, args);
    }
}

