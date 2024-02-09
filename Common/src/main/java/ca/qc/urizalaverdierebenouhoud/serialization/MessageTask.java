package ca.qc.urizalaverdierebenouhoud.serialization;

/**
 * Enum representing the different tasks that can be sent by the client to the server.
 */
public enum MessageTask {
    LOGIN,
    SEND_RECENT_HISTORY,
    SEND_MESSAGE,
    CLOSE_CONNECTION;

    /**
     * Converts a byte to a MessageTask
     * @param b the byte to convert
     * @return the corresponding MessageTask
     */
    public static MessageTask fromByte(byte b) {
        return switch (b) {
            case 3 -> LOGIN;
            case 1 -> SEND_RECENT_HISTORY;
            case 2 -> SEND_MESSAGE;
            case 4 -> CLOSE_CONNECTION;
            default -> throw new IllegalArgumentException("Invalid byte value for MessageTask");
        };
    }

    /**
     * Converts a MessageTask to a byte
     * @param task the MessageTask to convert
     * @return the corresponding byte
     */
    public static byte toByte(MessageTask task) {
        return switch (task) {
            case LOGIN -> 3;
            case SEND_RECENT_HISTORY -> 1;
            case SEND_MESSAGE -> 2;
            case CLOSE_CONNECTION -> 4;
            default -> throw new IllegalArgumentException("Invalid MessageTask value");
        };
    }
}
