import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;

/*
Aus der Library https://github.com/kwhat/jnativehook,
gefunden und zusammengefasst durch https://stackoverflow.com/questions/901224/listening-for-input-without-focus-in-java
 */
public class InputController implements NativeKeyListener {

    private Main app;
    private String screenshotKey;

    public InputController(Main app, String screenshotKey) {
        this.app = app;
        this.screenshotKey = screenshotKey;
    }

    public void setScreenshotKey(String key) {
        screenshotKey = key;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals(screenshotKey)){
            try {
                app.takeAndShowScreenshot("screenshot");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {

    }

    public void nativeKeyTyped(NativeKeyEvent e) {

    }
}
