package dev.khanh.plugin.vuongquoc;

import dev.khanh.plugin.kplugin.KPlugin;
import dev.khanh.plugin.kplugin.file.MessageFile;
import dev.khanh.plugin.kplugin.instance.InstanceManager;
import dev.khanh.plugin.vuongquoc.command.RealmAdminCommand;
import dev.khanh.plugin.vuongquoc.command.SpawnCommand;
import dev.khanh.plugin.vuongquoc.database.DatabaseManager;
import dev.khanh.plugin.vuongquoc.expansion.RealmExpansion;
import dev.khanh.plugin.vuongquoc.file.ConfigFile;
import dev.khanh.plugin.vuongquoc.listener.PlayerListener;
import dev.khanh.plugin.vuongquoc.realm.RealmManager;
import dev.khanh.plugin.vuongquoc.user.UserManager;
import lombok.Getter;

@Getter
public final class RealmPlugin extends KPlugin {
    private ConfigFile configFile;
    private MessageFile messageFile;
    private RealmManager realmManager;
    private DatabaseManager databaseManager;
    private UserManager userManager;

    private RealmExpansion expansion;

    private RealmAdminCommand realmAdminCommand;
    private SpawnCommand spawnCommand;

    @Override
    public void enable() {
        configFile = new ConfigFile(this);
        InstanceManager.registerInstance(ConfigFile.class, configFile);

        messageFile = new MessageFile(this);
        InstanceManager.registerInstance(MessageFile.class, messageFile);

        databaseManager = new DatabaseManager(this);
        InstanceManager.registerInstance(DatabaseManager.class, databaseManager);

        realmManager = new RealmManager(this);
        InstanceManager.registerInstance(RealmManager.class, realmManager);

        userManager = new UserManager(this);
        InstanceManager.registerInstance(UserManager.class, userManager);

        expansion = new RealmExpansion(this);
        expansion.register();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        realmAdminCommand = new RealmAdminCommand(this);
        spawnCommand = new SpawnCommand(this);
    }

    @Override
    public void disable() {
        if (expansion != null) {
            expansion.unregister();
        }

        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }
}
