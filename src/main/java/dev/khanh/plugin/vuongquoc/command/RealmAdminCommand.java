package dev.khanh.plugin.vuongquoc.command;

import dev.khanh.plugin.kplugin.command.BukkitCommand;
import dev.khanh.plugin.kplugin.util.ColorUtil;
import dev.khanh.plugin.kplugin.util.MessageUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.command.subcommand.SetRealmCommand;
import dev.khanh.plugin.vuongquoc.command.subcommand.SetSpawnCommand;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class RealmAdminCommand extends BukkitCommand {
    private final RealmPlugin plugin;

    public RealmAdminCommand(RealmPlugin plugin) {
        super("realmadmin", null, List.of("vuongquocadmin", "vqa"), "vuongquoc.admin",
                null, "/realmadmin &d<args>");

        try {
            addSubCommand(new SetRealmCommand(this, plugin));
            addSubCommand(new SetSpawnCommand(this, plugin));

            registerCommand(plugin);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, List<String> args) {
        sendHelpMessage(sender);
    }

    @Override
    public void onCommand(Player player, List<String> args) {
        onCommand((CommandSender) player, args);
    }

    public void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(
                Component.text("RealmAdmin", NamedTextColor.AQUA)
                        .append(Component.text(" Commands:", NamedTextColor.WHITE))
        );
        for (BukkitCommand subCommand: this.subCommands) {
            if (subCommand.getDescription().isEmpty()) {
                sender.sendMessage(Component.text(ColorUtil.colorize(subCommand.getUsage()), NamedTextColor.GRAY));
            } else {
                sender.sendMessage(
                        Component.text(ColorUtil.colorize(subCommand.getUsage()), NamedTextColor.GRAY)
                                .append(Component.text(": ", NamedTextColor.GRAY))
                                .append(Component.text(subCommand.getDescription(), NamedTextColor.YELLOW))
                );
            }
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
