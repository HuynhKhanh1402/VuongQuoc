package dev.khanh.plugin.vuongquoc.realm;

import com.google.common.base.Preconditions;
import dev.khanh.plugin.kplugin.util.LoggerUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.database.dao.UserDAO;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class RealmManager {
    private final RealmPlugin plugin;
    private final Map<String, Realm> realms = new HashMap<>();
    private final Map<String, Integer> realmMemberCount = new HashMap<>();

    public RealmManager(RealmPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSection realmsSection = plugin.getConfigFile().getConfigurationSection("realms");
        Preconditions.checkNotNull(realmsSection, "[config.yml] realms section is null");

        for (String realmName : realmsSection.getKeys(false)) {
            ConfigurationSection section = realmsSection.getConfigurationSection(realmName);
            Preconditions.checkNotNull(section, "[config.yml] realm " + realmName + " section is null");

            try {
                Realm realm = loadRealm(realmName, section);
                realms.put(realmName, realm);
                realmMemberCount.put(realmName, 0);
            } catch (Exception e) {
                throw new RuntimeException("[config.yml] failed to load realm " + realmName, e);
            }
        }

        LoggerUtil.info("Loaded " + realms.size() + " realms");

        registerGetRealmMemberCountTask();
    }

    private Realm loadRealm(String realmName, ConfigurationSection section) {
        String display = section.getString("display");
        String color = section.getString("color");
        Location location = section.getLocation("spawn-location");

        return new Realm(this, realmName, display, color, location);
    }

    public Optional<Realm> getRealm(String realmName) {
        return Optional.ofNullable(realms.get(realmName));
    }

    public int getRealmMemberCount(String realmName) {
        return realmMemberCount.get(realmName);
    }

    public int getRealmMemberCount(Realm realm) {
        return getRealmMemberCount(realm.getName());
    }

    private void registerGetRealmMemberCountTask() {
        UserDAO userDAO = plugin.getDatabaseManager().getUserDAO();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (String realm : realms.keySet()) {
                int memberCount = userDAO.getRealmMemberCount(realm).join();
                realmMemberCount.put(realm, memberCount);
            }
        }, 0, 5);
    }
}
