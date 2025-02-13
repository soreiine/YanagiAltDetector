package win.yanagi.yanagiAltDetector.Message;

public enum MessageKey {
    RELOADED("reloaded"),

    ERROR_PLAYER_NOT_FOUND("error.player-not-found"),

    COMMAND_ALTCHECK_FOUND("command.altcheck.found"),
    COMMAND_ALTCHECK_NOT_FOUND("command.altcheck.not-found"),

    ON_JOIN_FOUND("on-join.found"),
    ON_JOIN_NOT_FOUND("on-join.not-found"),

    FORMAT_STATUS_ONLINE("format.status.online"),
    FORMAT_STATUS_OFFLINE("format.status.offline"),
    FORMAT_ALT_INFO_ON_JOIN_MESSAGE("format.alt-info.on-join.message"),
    FORMAT_ALT_INFO_ON_JOIN_DELIMITER("format.alt-info.on-join.delimiter"),
    FORMAT_ALT_INFO_COMMAND_MESSAGE("format.alt-info.command.message"),
    FORMAT_ALT_INFO_COMMAND_DELIMITER("format.alt-info.command.delimiter");

    private final String key;

    private MessageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}