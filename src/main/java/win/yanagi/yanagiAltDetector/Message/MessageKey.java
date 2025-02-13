package win.yanagi.yanagiAltDetector.Message;

public enum MessageKey {
    RELOADED("reloaded"),

    ERROR_PLAYER_NOT_FOUND("error.player-not-found"),

    COMMAND_ALTCHECK_FOUND("command.altcheck.found"),
    COMMAND_ALTCHECK_NOT_FOUND("command.altcheck.not-found"),
    COMMAND_ALTCHECK_LOG_FOUND("command.altcheck.log.found"),
    COMMAND_ALTCHECK_LOG_NOT_FOUND("command.altcheck.log.not_found"),

    ON_JOIN_FOUND("on-join.found"),
    ON_JOIN_NOT_FOUND("on-join.not-found"),
    ON_JOIN_LOG_FOUND("on-join.log.found"),
    ON_JOIN_LOG_NOT_FOUND("on-join.log.not-found"),

    FORMAT_STATUS_ONLINE("format.status.online"),
    FORMAT_STATUS_OFFLINE("format.status.offline"),
    FORMAT_ALT_INFO_ON_JOIN_MESSAGE("format.alt-info.on-join.message"),
    FORMAT_ALT_INFO_ON_JOIN_SEPARATOR("format.alt-info.on-join.separator"),
    FORMAT_ALT_INFO_ON_JOIN_LOG_MESSAGE("format.alt-info.on-join.log.message"),
    FORMAT_ALT_INFO_ON_JOIN_LOG_SEPARATOR("format.alt-info.on-join.log.separator"),
    FORMAT_ALT_INFO_COMMAND_ALTCHECK_MESSAGE("format.alt-info.command.altcheck.message"),
    FORMAT_ALT_INFO_COMMAND_ALTCHECK_SEPARATOR("format.alt-info.command.altcheck.separator"),
    FORMAT_ALT_INFO_COMMAND_ALTCHECK_LOG_MESSAGE("format.alt-info.command.altcheck.log.message"),
    FORMAT_ALT_INFO_COMMAND_ALTCHECK_LOG_SEPARATOR("format.alt-info.command.altcheck.log.separator");

    private final String key;

    private MessageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}