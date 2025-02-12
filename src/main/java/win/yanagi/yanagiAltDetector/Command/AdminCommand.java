package win.yanagi.yanagiAltDetector.Command;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import win.yanagi.yanagiAltDetector.Message.MessageKey;
import win.yanagi.yanagiAltDetector.YanagiAltDetector;

public class AdminCommand {
    private final YanagiAltDetector plugin;

    public AdminCommand(YanagiAltDetector plugin) {
        this.plugin = plugin;
    }

    // /alt <target> の処理部分
    @Command("alt")
    @CommandPermission("yanagialtdetector.checkalt")
    public void seeAlts(BukkitCommandActor actor, String target) {
        String targetUUID = plugin.getDatabase().getUUID(target);

        if (targetUUID.equals("")) {
            plugin.getMessageManager().send(actor.sender(), MessageKey.ERROR_PLAYER_NOT_FOUND);
            return;
        }

        plugin.getAltDetectionManager().getAlt(targetUUID, (altsList) -> {
            if (!altsList.isEmpty()) {
                plugin.getMessageManager().send(actor.sender(), MessageKey.COMMAND_ALTCHECK_FOUND,
                        "player", target,
                        "alt-info", plugin.getAltDetectionManager().getFormattedAltInfo(altsList, MessageKey.FORMAT_ALT_INFO_COMMAND_MESSAGE, MessageKey.FORMAT_ALT_INFO_COMMAND_DELIMITER)
                );
            } else {
                plugin.getMessageManager().send(actor.sender(), MessageKey.COMMAND_ALTCHECK_NOT_FOUND, "player", target);
            }
        });
    }

    // /yad reload, /yanagialtdetector reload の処理部分
    @Command({"yad reload", "yanagialtdetector reload"})
    @CommandPermission("yanagialtdetector.admin")
    public void reload(BukkitCommandActor actor) {
        plugin.getMessageManager().load();

        plugin.getMessageManager().send(actor.sender(), MessageKey.RELOADED);
    }
}
