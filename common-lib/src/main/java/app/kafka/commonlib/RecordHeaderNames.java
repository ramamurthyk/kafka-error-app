package app.kafka.commonlib;

public abstract class RecordHeaderNames {
    public static final String PREFIX = "x_";
    public final static String MESSAGE_ID = PREFIX + "messageId";
    public final static String MESSAGE_TYPE = PREFIX + "messageType";
}
