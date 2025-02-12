package win.yanagi.yanagiAltDetector.Message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import win.yanagi.yanagiAltDetector.YanagiAltDetector;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageManager {
    private final YanagiAltDetector plugin;
    private FileConfiguration messages;

    public MessageManager(YanagiAltDetector plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public @Nullable String getRawString(MessageKey key, Object... replacers) {
        String rawMessage = messages.getString(key.getKey());
        if (rawMessage == null) {
            return null;
        }

        return format(rawMessage, replacers);
    }

    public @Nullable Component getRawComponent(MessageKey key, Object... replacers) {
        String rawMessage = messages.getString(key.getKey());
        if (rawMessage == null || rawMessage.trim().isEmpty()) return null;

        String formatted = format(rawMessage, replacers);

        return formatted != null ? Component.text(formatted) : Component.empty();
    }

    public @Nullable String getString(MessageKey key, Object... replacers) {
        String rawMessage = messages.getString(key.getKey());
        if (rawMessage == null) {
            return null;
        }
        String formatted = format(rawMessage, replacers);
        if (formatted == null) {
            return null;
        }

        Component component = MiniMessage.miniMessage().deserialize(formatted);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public @Nullable Component getComponent(MessageKey key, Object... replacers) {
        String rawMessage = messages.getString(key.getKey());
        if (rawMessage == null || rawMessage.trim().isEmpty()) return null;

        MiniMessage miniMessage = MiniMessage.miniMessage();
        String formatted = format(rawMessage, replacers);

        return formatted != null ? miniMessage.deserialize(formatted) : null;
    }

    public @Nullable List<String> getStringList(MessageKey key, Object... replacers) {
        String message = getString(key, replacers);
        return message == null ? null : Stream.of(message.split("\n")).map(String::trim).collect(Collectors.toList());
    }

    public @Nullable List<Component> getComponentList(MessageKey key, Object... replacers) {
        Component message = getComponent(key, replacers);
        if (message == null) return null;

        String messageStr = MiniMessage.miniMessage().serialize(message);

        return Arrays.stream(messageStr.split("\n")).map(String::trim).map(line -> MiniMessage.miniMessage().deserialize(line)).collect(Collectors.toList());
    }

    public void send(CommandSender receiver, MessageKey key, Object... replacers) {
        Component message = getComponent(key, replacers);
        if (message != null && !message.equals(Component.empty())) {
            receiver.sendMessage(message);
        }
    }

    public void send(List<? extends CommandSender> receivers, MessageKey key, Object... replacers) {
        Component message = getComponent(key, replacers);
        if (message != null) {
            receivers.forEach(receiver -> receiver.sendMessage(message));
        }
    }


    private @Nullable String format(String message, Object... replacers) {
        if (message == null) return null;

        for (int i = 0; i < replacers.length; i += 2) {
            if (i + 1 < replacers.length) {
                message = message.replace("%" + replacers[i] + "%", String.valueOf(replacers[i + 1]));
            }
        }
        return message;
    }
}
