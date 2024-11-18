package dev.khanh.plugin.vuongquoc.user;

import dev.khanh.plugin.kplugin.instance.InstanceManager;
import dev.khanh.plugin.vuongquoc.database.DatabaseManager;
import dev.khanh.plugin.vuongquoc.database.dao.UserDAO;
import dev.khanh.plugin.vuongquoc.realm.Realm;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class User {
    private final String userName;
    @Nullable
    private Realm realm;
    private final OfflinePlayer player;

    private final UserDAO userDAO;

    public User(String userName, @Nullable Realm realm) {
        this.player = findPlayerByName(userName);
        this.userName = userName;
        this.realm = realm;

        this.userDAO = InstanceManager.getInstanceOrElseThrow(DatabaseManager.class).getUserDAO();
    }

    private OfflinePlayer findPlayerByName(String playerName) {
        if (Bukkit.getPlayerExact(playerName) != null) {
            return Bukkit.getPlayerExact(playerName);
        }
        if (Bukkit.getOfflinePlayerIfCached(playerName) != null) {
            return Bukkit.getOfflinePlayerIfCached(playerName);
        }
        return Bukkit.getOfflinePlayer(playerName);
    }

    public CompletableFuture<Void> setRealm(@Nullable Realm realm) {
        this.realm = realm;

        return userDAO.upsertAsync(this);
    }
}
