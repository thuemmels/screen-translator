import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PositioningButtonListener implements ActionListener {
    private Main main;

    public PositioningButtonListener(Main main){
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            main.takeAndShowScreenshot("positioning");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
