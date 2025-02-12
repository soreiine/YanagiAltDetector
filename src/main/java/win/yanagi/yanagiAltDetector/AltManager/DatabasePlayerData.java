package win.yanagi.yanagiAltDetector.AltManager;

import java.util.UUID;

// データベースからのプレイヤーデータ返送用のレコード
public record DatabasePlayerData(String name, UUID uuid) {}