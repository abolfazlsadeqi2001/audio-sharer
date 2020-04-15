package sockets.stream;

public class StreamWebSocketParent {
	protected static final int MAX_BINARRY_MESSAGE = 400 * 1024;// the heaviest size that has been gained
	protected static final int MAX_TEXT_MESSAGE = 1024;// 1KB as default
	protected static final int MAX_TIME_OUT = 40 * 1000;
}
