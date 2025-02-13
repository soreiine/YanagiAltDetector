package win.yanagi.yanagiAltDetector;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;
import win.yanagi.yanagiAltDetector.AltManager.AltDetectionManager;
import win.yanagi.yanagiAltDetector.Command.AdminCommand;
import win.yanagi.yanagiAltDetector.Database.Database;
import win.yanagi.yanagiAltDetector.Listener.PlayerJoinListener;
import win.yanagi.yanagiAltDetector.Message.MessageManager;

import java.io.File;
import java.sql.SQLException;

public final class YanagiAltDetector extends JavaPlugin {
    private MessageManager messageManager;
    private Database database;
    private AltDetectionManager altDetectionManager;

    @Override
    public void onEnable() {
        // MessageManager
        messageManager = new MessageManager(this);
        messageManager.load();

        // Database
        try {
            database = new Database(this, getDataFolder().getAbsolutePath() + File.separator + "database.db");
        } catch (SQLException exception) {
            getLogger().severe("データベースエラー: データベースの準備中に発生 | " + exception.getMessage());
        }

        // AltDetectionManager
        altDetectionManager = new AltDetectionManager(this);

        // Listener
        new PlayerJoinListener(this);

        // Command
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new AdminCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException exception) {
            getLogger().severe("データベースエラー: データベースから切断中に発生 | " + exception.getMessage());
        }
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public Database getDatabase() {
        return database;
    }

    public AltDetectionManager getAltDetectionManager() {
        return altDetectionManager;
    }
}