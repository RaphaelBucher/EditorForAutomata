package editor;

public class Message {
  private String message = "";
  private long messageCreationTimeStamp = 0L;
  private long messageDurationMillis;
  
  public Message(String message, long messageDurationMillis) {
    this.message = message;
    this.messageCreationTimeStamp = System.currentTimeMillis();
    this.messageDurationMillis = messageDurationMillis;
  }
  
  /**
   * Simulates the fade effect. The first half of the message duration, full Color is shown.
   * During the second half, the message fades away by reducing the alpha from 255 to 0.
   */
  public int getFadeAlpha() {
    // For e.g. ERROR_MESSAGE_DURATION_MILLIS = 3000, goes down from 3000 to 0
    // Negative values possible, will be dealt with later
    long remainingMillis = this.messageDurationMillis
        - (System.currentTimeMillis() - this.messageCreationTimeStamp);

    // fade effect starts at halftime. So for e.g. ERROR_MESSAGE_DURATION_MILLIS
    // = 3000, it goes down from 255 * 2 to 0 and limits result to 255
    int alpha = (int)((double)remainingMillis / this.messageDurationMillis * 255 * 2);

    // limit the value to 255 and ensure its not negative
    alpha = Math.min(alpha, 255);
    alpha = Math.max(alpha, 0);

    return alpha;
  }
  
  public void update() {
    if (System.currentTimeMillis() - this.messageCreationTimeStamp > this.messageDurationMillis) {
      this.setMessage("");
    }
  }
  
  // Getters and Setters
  public void setMessage(String message) {
    this.message = message;
    messageCreationTimeStamp = System.currentTimeMillis();
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public long getMessageCreationTimeStamp() {
    return this.messageCreationTimeStamp;
  }
}
