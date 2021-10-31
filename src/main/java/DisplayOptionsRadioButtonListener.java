import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayOptionsRadioButtonListener implements ActionListener {
    private JTextArea textArea;
    private JButton positionButton;

    public DisplayOptionsRadioButtonListener(JTextArea textArea, JButton positionButton){
        this.textArea = textArea;
        this.positionButton = positionButton;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "separated":
                Main.getInstance().getMainWindow().setDisplayMode("separated");
                textArea.setText("Im \"separaten Fenster\"-Modus erscheint das Fenster mit dem übersetzten Text im Hintergrund.");
                positionButton.setEnabled(false);
                break;
            case "overlapping":
                Main.getInstance().getMainWindow().setDisplayMode("overlapping");
                textArea.setText("Im \"überlappenden Fenster\"-Modus erscheint das Fenster mit dem übersetzten Text im Vordergrund. " +
                        "Die Position und Größe kann nach Belieben angepasst werden.");
                positionButton.setEnabled(true);
                break;
        }
    }
}
