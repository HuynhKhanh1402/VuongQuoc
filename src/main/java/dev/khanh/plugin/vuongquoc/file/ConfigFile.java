package dev.khanh.plugin.vuongquoc.file;

import dev.khanh.plugin.kplugin.file.AbstractConfigFile;
import dev.khanh.plugin.kplugin.util.LoggerUtil;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.util.WGUtils;
import lombok.Getter;

import java.util.List;

@Getter
public class ConfigFile extends AbstractConfigFile {
    private final boolean allowRealmMemberPvp;
    private final List<String> allowRealmMemberPvPRegions;
    private final String spawnFallbackCommand;

    public ConfigFile(RealmPlugin plugin) {
        super(plugin);

        allowRealmMemberPvp = getBoolean("general.allow-realm-member-pvp");

        allowRealmMemberPvPRegions = getStringList("general.allow-realm-member-pvp-regions");
        allowRealmMemberPvPRegions.forEach(s -> {
            if (!WGUtils.regionExists(s)) {
                LoggerUtil.warning("Not found region: %s (general.allow-realm-member-pvp-regions)".formatted(s));
            }
        });

        spawnFallbackCommand = getString("general.spawn-fallback-command");
    }

    @Override
    public void update(int currentVersion, int defaultVersion) {

    }
}
