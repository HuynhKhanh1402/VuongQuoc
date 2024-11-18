package dev.khanh.plugin.vuongquoc.user;

import dev.khanh.plugin.kplugin.instance.InstanceManager;
import dev.khanh.plugin.vuongquoc.RealmPlugin;
import dev.khanh.plugin.vuongquoc.database.DatabaseManager;
import dev.khanh.plugin.vuongquoc.database.dao.UserDAO;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserManager {
    private final RealmPlugin plugin;
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public UserManager(RealmPlugin plugin) {
        this.plugin = plugin;

        // Load data for online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                loadUser(player).join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public User getUser(String username) {
        return userMap.get(username);
    }

    @NotNull
    public User getUser(Player player) {
        User user = getUser(player.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + player.getName());
        }
        return user;
    }

    public void addUser(User user) {
        userMap.put(user.getUserName(), user);
    }

    public void removeUser(String username) {
        userMap.remove(username);
    }

    public CompletableFuture<User> loadUser(Player player) {
        UserDAO userDAO = InstanceManager.getInstanceOrElseThrow(DatabaseManager.class).getUserDAO();

        return CompletableFuture.supplyAsync(() -> {
            User user = userDAO.get(player.getName()).orElse(new User(player.getName(), null));
            addUser(user);
            return user;
        });
    }
}
