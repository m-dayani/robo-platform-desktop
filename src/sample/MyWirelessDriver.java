package sample;

public class MyWirelessDriver {

    public static final String DEFAULT_TEST_COMMAND = "wl-8749";
    public static final String DEFAULT_TEST_RESPONSE = "wl-0462";

    private static final String CMD_SEPARATOR_CHAR = "#";

    public enum WirelessCommand {
        BROADCAST,
        CMD_DIR,
        CMD_CHAR,
        CMD_WORD,
        SENSOR,
        TEST,
        CHAT
    }

    public static class WirelessMsg {

        WirelessMsg(WirelessCommand cmd, String message) {

            mCmd = cmd;
            mMessage = message;
        }

        public WirelessCommand mCmd;
        public String mMessage;
    }

    public static String encodeMessage(WirelessMsg msg) {

        return cmdToString(msg.mCmd)+CMD_SEPARATOR_CHAR+msg.mMessage;
    }

    public static WirelessMsg decodeMessage(String msg) {

        WirelessMsg wlMsg = new WirelessMsg(WirelessCommand.BROADCAST, "");
        String[] msgParts = msg.split(CMD_SEPARATOR_CHAR);

        if (msgParts.length < 2) {
            return wlMsg;
        }

        return new WirelessMsg(stringToCmd(msgParts[0]), msgParts[1]);
    }

    public static boolean matchesTest(WirelessMsg msg) {

        return msg != null && WirelessCommand.TEST.equals(msg.mCmd) && DEFAULT_TEST_COMMAND.matches(msg.mMessage);
    }

    public static String getTestResponse() {

        return encodeMessage(new WirelessMsg(WirelessCommand.TEST, DEFAULT_TEST_RESPONSE));
    }

    public static String cmdToString(WirelessCommand cmd) {

        switch (cmd) {
            case CMD_DIR:
                return "dir";
            case CMD_CHAR:
                return "char";
            case CMD_WORD:
                return "word";
            case TEST:
                return "test";
            case SENSOR:
                return "sensor";
            case CHAT:
                return "chat";
            case BROADCAST:
            default:
                return "misc";
        }
    }

    public static WirelessCommand stringToCmd(String cmd) {

        switch (cmd) {
            case "dir":
                return WirelessCommand.CMD_DIR;
            case "char":
                return WirelessCommand.CMD_CHAR;
            case "word":
                return WirelessCommand.CMD_WORD;
            case "test":
                return WirelessCommand.TEST;
            case "sensor":
                return WirelessCommand.SENSOR;
            case "chat":
                return WirelessCommand.CHAT;
            case "misc":
            default:
                return WirelessCommand.BROADCAST;
        }
    }
}
