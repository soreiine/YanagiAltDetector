package win.yanagi.yanagiAltDetector.Database;

import win.yanagi.yanagiAltDetector.AltManager.DatabasePlayerData;
import win.yanagi.yanagiAltDetector.YanagiAltDetector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private final YanagiAltDetector plugin;
    private final Connection connection;

    public Database(YanagiAltDetector plugin, String path) throws SQLException {
        this.plugin = plugin;

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS playertable (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, uuid CHAR(36) UNIQUE NOT NULL, name VARCHAR(255) NOT NULL)");
            statement.execute("CREATE TABLE IF NOT EXISTS iptable (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ip VARCHAR(255) NOT NULL, playerid INTEGER NOT NULL REFERENCES playertable(id) ON DELETE CASCADE, date DATETIME NOT NULL)");
            statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS uuid_index ON playertable(uuid)");
            statement.execute("CREATE INDEX IF NOT EXISTS ip_index ON iptable (ip)");
            statement.execute("CREATE INDEX IF NOT EXISTS playerid_index ON iptable (playerid)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public String getUUID(String name) {
        String uuid = "";

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM playertable WHERE name LIKE ?")) {
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    uuid = resultSet.getString("uuid");
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: プレイヤーのUUIDを取得中に発生 | " + exception.getMessage());
        }

        return uuid;
    }

    public String getName(String uuid) {
        String name = "";

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM playertable WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    name = resultSet.getString("name");
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: プレイヤー名を取得中に発生 | " + exception.getMessage());
        }

        return name;
    }

    public boolean addPlayer(String name, String uuid) {
        boolean success = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO playertable (uuid, name) VALUES (?, ?)")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            success = true;
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: ユーザーを追加中に発生 | " + exception.getMessage());
        }

        return success;
    }

    public boolean updateName(String name, String uuid) {
        boolean success = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE playertable SET name = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, uuid);
            preparedStatement.executeUpdate();
            success = true;
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: ユーザー名を更新中に発生 | " + exception.getMessage());
        }

        return success;
    }

    public boolean ipExists(String ip, String uuid) {
        boolean success = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS (SELECT 1 FROM iptable INNER JOIN playertable ON iptable.playerid = playertable.id WHERE ip = ? AND uuid = ?)")) {
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, uuid);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                success = resultSet.getBoolean(1);
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: " + ip + " と" + uuid + " の存在を確認中に発生 | " + exception.getMessage());
        }

        return success;
    }

    public boolean addIp(String ip, String uuid) {
        boolean success = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO iptable (ip, playerid, date) VALUES (?, (SELECT id FROM playertable WHERE uuid = ?), datetime('now', 'localtime'))")) {
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, uuid);
            preparedStatement.executeUpdate();
            success = true;
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: " + ip + " と" + uuid + " の情報を追加中に発生 | " + exception.getMessage());
        }

        return success;
    }

    public boolean updateLastLoginByIp(String ip, String uuid) {
        boolean success = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE iptable SET date = datetime('now', 'localtime') WHERE ip = ? AND playerid = (SELECT id FROM playertable WHERE uuid = ?)")) {
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, uuid);
            preparedStatement.executeUpdate();
            success = true;
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: " + ip + " と" + uuid + " の情報を更新中に発生 | " + exception.getMessage());
        }

        return success;
    }

    public List<DatabasePlayerData> getAltData(String uuid, String excludeUuid) {
        List<DatabasePlayerData> altList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT name, uuid FROM iptable INNER JOIN playertable ON iptable.playerid = playertable.id WHERE ip IN (SELECT ip FROM iptable INNER JOIN playertable ON iptable.playerid = playertable.id WHERE uuid = ?) AND uuid != ? ORDER BY lower(name)")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, excludeUuid);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String targetName = resultSet.getString("name");
                    String targetUuid = resultSet.getString("uuid");

                    altList.add(new DatabasePlayerData(targetName, UUID.fromString(targetUuid)));
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: " + uuid + " と同じ情報を持つプレイヤーを確認中に発生 | " + exception.getMessage());
        }

        return altList;
    }

    public String getLastJoin(String ip, String uuid) {
        String date = "";

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT date FROM iptable INNER JOIN playertable ON iptable.playerid = playertable.id WHERE ip = ? AND uuid = ?")) {
            preparedStatement.setString(1, ip);
            preparedStatement.setString(2, uuid);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    date = resultSet.getString("date");
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("データベースエラー: " + ip + " と" + uuid + " の最終ログインを取得中に発生" + exception.getMessage());
        }

        return date;
    }
}