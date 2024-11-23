package dev.khanh.plugin.vuongquoc.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import dev.khanh.plugin.kplugin.instance.InstanceManager;
import dev.khanh.plugin.vuongquoc.realm.Realm;
import dev.khanh.plugin.vuongquoc.realm.RealmManager;
import dev.khanh.plugin.vuongquoc.user.User;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Getter
public class UserDAO {
    private final HikariDataSource dataSource;
    private static final String TABLE_NAME = "USERS";

    public UserDAO(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> get(String playerName) {
        RealmManager realmManager = InstanceManager.getInstanceOrElseThrow(RealmManager.class);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE username = ?");
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String realmName = resultSet.getString("REALM");

                if (realmName == null) {
                    return Optional.of(new User(playerName, null));
                } else {
                    Realm realm = realmManager.getRealm(realmName).orElseThrow(
                            () -> new IllegalArgumentException("Realm not found: " + realmName));
                    return Optional.of(new User(playerName, realm));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Optional<User>> getAsync(String playerName) {
        return CompletableFuture.supplyAsync(() -> get(playerName))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException(throwable);
                });
    }


    public void upsert(User user) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT OR REPLACE INTO " + TABLE_NAME + " (username, realm) VALUES (?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getRealm() == null ? null : user.getRealm().getName());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> upsertAsync(User user) {
        return CompletableFuture.runAsync(() -> upsert(user))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException();
                });
    }

    public void delete(User user) {
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE username = ?");
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> deleteAsync(User user) {
        return CompletableFuture.runAsync(() -> delete(user))
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    throw new RuntimeException();
                });
    }

    public CompletableFuture<Integer> getRealmMemberCount(String realm) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS MEMBERS FROM " + TABLE_NAME + " WHERE realm = ?");
                preparedStatement.setString(1, realm);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("MEMBERS");
                }
                return 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            throw new RuntimeException();
        });
    }
}
