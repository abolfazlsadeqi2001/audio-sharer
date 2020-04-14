package configurations.sound.streaming;

public class DefaultStreamingValues {
	private static final int DELAY = 20;
	private static final String MIME_TYPE = "audio/ogg";
	
	public static int getDelay() {
		return DELAY;
	}
	
	public static String getMimeType() {
		return MIME_TYPE;
	}
	
	
}
