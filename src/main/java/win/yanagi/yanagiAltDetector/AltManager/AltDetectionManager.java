package win.yanagi.yanagiAltDetector.AltManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import win.yanagi.yanagiAltDetector.Message.MessageKey;
import win.yanagi.yanagiAltDetector.Message.MessageManager;
import win.yanagi.yanagiAltDetector.YanagiAltDetector;

import java.util.List;
import java.util.function.Consumer;

public class AltDetectionManager {
    private final YanagiAltDetector plugin;

    public AltDetectionManager(YanagiAltDetector plugin) {
        this.plugin = plugin;
    }

    // 現在のセッション情報をデータベースへ記録
    public void updateDatabase(String name, String uuid, String ip, Consumer<List<DatabasePlayerData>> callback) {
        // BukkitのSchedulerで非同期で実行（DB処理はメインスレッドをブロックする可能性があるため）
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String gotName = plugin.getDatabase().getName(uuid);

            if (gotName.equals("")) { // DBから取得したユーザー名が存在しなかったら（登録されていなかったら）
                plugin.getDatabase().addPlayer(name, uuid);
            } else if (!gotName.equals(name)) { // DBから取得したユーザー名が現在のユーザー名と違ったら（名前が変更されていたら）
                plugin.getDatabase().updateName(name, uuid);
            }

            boolean isIpExists = plugin.getDatabase().ipExists(ip, uuid);

            if (!isIpExists) { // DBにプレイヤーと現在のIPでの組み合わせが登録されていなかったら
                plugin.getDatabase().addIp(ip, uuid);
            } else { // DBにプレイヤーと現在のIPでの組み合わせが登録されていたら
                // 現在のIPとUUIDの組み合わせの最終ログイン時間を更新
                plugin.getDatabase().updateLastLoginByIp(ip, uuid);
            }

            // DBからプレイヤーと同IPのプレイヤー名のListを取得
            List<DatabasePlayerData> alt = plugin.getDatabase().getAltData(uuid, uuid);

            // callback引数に指定された処理を、取得後に同期的に実行
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(alt));
        });
    }

    // 指定したプレイヤーのサブアカウントである可能性があるアカウントを取得
    public void getAlt(String uuid, Consumer<List<DatabasePlayerData>> callback) {
        // BukkitのSchedulerで非同期で実行（DB処理はメインスレッドをブロックする可能性があるため）
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // DBからプレイヤーと同IPのプレイヤー名のListを取得
            List<DatabasePlayerData> alt = plugin.getDatabase().getAltData(uuid, uuid);

            // callback引数に指定された処理を、取得後に同期的に実行
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(alt));
        });
    }

    // DatabasePlayerDataの情報をymlファイルに書かれたフォーマットに置き換え
    public String getFormattedAltInfo(List<DatabasePlayerData> altList, MessageKey formatKey, MessageKey delimiter) {
        if (altList.isEmpty()) {
            return "";
        }

        MessageManager messageManager = plugin.getMessageManager();
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for (DatabasePlayerData altData : altList) {
            i++;

            Player targetAlt = Bukkit.getPlayer(altData.uuid());
            MessageKey statusKey = targetAlt != null && targetAlt.isOnline() ? MessageKey.FORMAT_STATUS_ONLINE : MessageKey.FORMAT_STATUS_OFFLINE;

            stringBuilder.append(messageManager.getRawString(formatKey,
                    "player", altData.name(),
                    "uuid", altData.uuid(),
                    "status", messageManager.getRawString(statusKey),
                    "position", i
            ));

            if (altList.size() > i) {
                stringBuilder.append(messageManager.getRawString(delimiter));
            }
        }

        return stringBuilder.toString();
    }
}