package win.yanagi.yanagiAltDetector.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import win.yanagi.yanagiAltDetector.Message.MessageKey;
import win.yanagi.yanagiAltDetector.YanagiAltDetector;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerJoinListener implements Listener {
    private final YanagiAltDetector plugin;

    public PlayerJoinListener(YanagiAltDetector plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String uuid = player.getUniqueId().toString();

        InetSocketAddress playerInetAddress = player.getAddress();
        if (playerInetAddress != null) {
            String ip = playerInetAddress.getAddress().getHostAddress();

            plugin.getAltDetectionManager().updateDatabase(name, uuid, ip, (altsList) -> {
                List<Player> ops = Bukkit.getOnlinePlayers().stream().filter(Player::isOp).collect(Collectors.toList());
                if (!altsList.isEmpty()) { // 同IPの参加者が見つかったら
                    plugin.getMessageManager().sendLog(Level.WARNING, MessageKey.ON_JOIN_LOG_FOUND,
                            "player", name,
                            "alt-info", plugin.getAltDetectionManager().getFormattedAltInfo(altsList, MessageKey.FORMAT_ALT_INFO_ON_JOIN_LOG_MESSAGE, MessageKey.FORMAT_ALT_INFO_ON_JOIN_LOG_SEPARATOR)
                    );

                    plugin.getMessageManager().send(ops, MessageKey.ON_JOIN_FOUND,
                            "player", name,
                            "alt-info", plugin.getAltDetectionManager().getFormattedAltInfo(altsList, MessageKey.FORMAT_ALT_INFO_ON_JOIN_MESSAGE, MessageKey.FORMAT_ALT_INFO_ON_JOIN_SEPARATOR)
                    );
                } else { // 同IPの参加者が見つからなかったら
                    plugin.getMessageManager().sendLog(Level.INFO, MessageKey.ON_JOIN_LOG_NOT_FOUND, "player", name);
                    plugin.getMessageManager().send(ops, MessageKey.ON_JOIN_NOT_FOUND, "player", name);
                }
            });
        }
    }
}