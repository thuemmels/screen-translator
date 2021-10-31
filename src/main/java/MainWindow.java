import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JPanel implements ActionListener, KeyListener {
    private JFrame frame;
    private Settings settings;
    private Main main;
    private JButton screenshotKeyBindButton;
    private JTextField screenshotKeyBindText;
    private JLabel targetLanguageLabel;
    private JComboBox targetLanguageBox;
    private boolean waitingForButtonInput;
    private int separateWindowPositionX;
    private int separateWindowPositionY;
    private int separateWindowWidth;
    private int separateWindowHeight;
    private String displayMode;
    private boolean positionCustomized = false;

    public MainWindow(JFrame frame, Settings settings, Main main){
        this.frame = frame;
        this.settings = settings;
        this.main = main;
    }

    private void changeWaitingForButtonInput(){
        if (waitingForButtonInput) {
            screenshotKeyBindText.setBackground(Color.GRAY);
        } else {
            screenshotKeyBindText.setBackground(Color.WHITE);
        }
        waitingForButtonInput = !waitingForButtonInput;
    }

    private void setUpDisplayOptions(){
        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel displayOptionsPanel = new JPanel();
        displayOptionsPanel.setLayout(new BoxLayout(displayOptionsPanel, BoxLayout.PAGE_AXIS));

        contentPane.add(displayOptionsPanel, BorderLayout.WEST);

        Border displayOptionsBorder = BorderFactory.createTitledBorder("Wie soll die Übersetzung angezeigt werden?");
        contentPane.setBorder(displayOptionsBorder);

        JRadioButton separatedWindowButton = new JRadioButton("Separates Fenster");
        separatedWindowButton.setActionCommand("separated");

        JRadioButton overlappingWindowButton = new JRadioButton("Überlappendes Fenster");
        overlappingWindowButton.setActionCommand("overlapping");

        ButtonGroup displayOptions = new ButtonGroup();
        displayOptions.add(separatedWindowButton);
        displayOptions.add(overlappingWindowButton);

        JTextArea explanatoryTextArea = new JTextArea(5,20);
        explanatoryTextArea.setEditable(false);
        explanatoryTextArea.setLineWrap(true);
        explanatoryTextArea.setWrapStyleWord(true);

        JButton positioningButton = new JButton("Position bestimmen");

        separatedWindowButton.addActionListener(new DisplayOptionsRadioButtonListener(explanatoryTextArea, positioningButton));
        overlappingWindowButton.addActionListener(new DisplayOptionsRadioButtonListener(explanatoryTextArea, positioningButton));
        positioningButton.addActionListener(new PositioningButtonListener(main));

        displayOptionsPanel.add(separatedWindowButton);
        displayOptionsPanel.add(overlappingWindowButton);
        contentPane.add(positioningButton, BorderLayout.PAGE_END);

        contentPane.add(explanatoryTextArea);

        separatedWindowButton.doClick();
        add(contentPane);
    }

    private void setUpFrame(){
        frame.setTitle("Bildschirmübersetzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400 ,300));
        frame.setLocationRelativeTo(null);
    }

    private void setUpScreenshotKeyBindButton(){
        screenshotKeyBindButton = new JButton();
        screenshotKeyBindButton.setText("Screenshot machen");
        screenshotKeyBindButton.setToolTipText("Klicke auf den Button, um die zugehörige Taste zu ändern");
        screenshotKeyBindButton.setActionCommand("screenshot");
    }

    private void setUpScreenshotKeyBindText(){
        screenshotKeyBindText = new JTextField(20);
        screenshotKeyBindText.setEditable(false);
        screenshotKeyBindText.setText(settings.getPropertyValue("screenshotKey"));
        screenshotKeyBindText.setBackground(Color.GRAY);
        screenshotKeyBindText.setHorizontalAlignment(JTextField.CENTER);
    }

    private void setUpScreenshotKeyBindUI(){
        JPanel screenshotKeyBindPanel = new JPanel();

        setUpScreenshotKeyBindButton();
        setUpScreenshotKeyBindText();

        screenshotKeyBindButton.addActionListener(this);
        screenshotKeyBindButton.addKeyListener(this);
        screenshotKeyBindButton.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if(waitingForButtonInput) changeWaitingForButtonInput();
            }
        });


        screenshotKeyBindPanel.add(screenshotKeyBindButton);
        screenshotKeyBindPanel.add(screenshotKeyBindText);
        add(screenshotKeyBindPanel);
    }

    private void setUpTargetLanguageBox(){
        targetLanguageBox = new JComboBox<>(TranslateText.getSupportedTargetLanguageNames());
        targetLanguageBox.setSelectedItem(TranslateText.getSupportedTargetLanguageName(settings.getPropertyValue("targetLanguage")));
        targetLanguageBox.setEditable(false);
        targetLanguageBox.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            updateTargetLanguage((String)cb.getSelectedItem());
        });
    }

    private void setUpTargetLanguageUI() {
        JPanel targetLanguagePanel = new JPanel();
        targetLanguageLabel = new JLabel("Zielsprache");
        setUpTargetLanguageBox();

        targetLanguagePanel.add(targetLanguageLabel);
        targetLanguagePanel.add(targetLanguageBox);
        add(targetLanguagePanel);
    }

    private void updateTargetLanguage(String newTargetLanguageName){
        String newTargetLanguageCode = TranslateText.getSupportedTargetLanguageCode(newTargetLanguageName);
        settings.setPropertyValue("targetLanguage", newTargetLanguageCode);
        TranslateText.setTargetLanguage(newTargetLanguageCode);
        settings.saveProperties();
    }

    public String getDisplayMode(){
        return displayMode;
    }

    public Point getSeparateWindowPosition() {
        return new Point(separateWindowPositionX, separateWindowPositionY);
    }

    public Dimension getSeparateWindowSize(){ return new Dimension(separateWindowWidth, separateWindowHeight); }

    public boolean isPositionCustomized() {
        return positionCustomized;
    }

    public void setDisplayMode(String mode){
        displayMode = mode;
    }

    public void setUp(){
        setUpFrame();
        add(new JLabel("Einstellungen"));
        setUpScreenshotKeyBindUI();
        setUpTargetLanguageUI();
        setUpDisplayOptions();
    }

    public void setSeparateWindowPosition(int x, int y){
        positionCustomized = true;
        separateWindowPositionX = x;
        separateWindowPositionY = y;
    }

    public void setSeparateWindowSize(int width, int height){
        separateWindowWidth = width;
        System.out.println("Neue Breite: " + width);
        separateWindowHeight = height;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("screenshot".equals(e.getActionCommand())) {
            changeWaitingForButtonInput();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(waitingForButtonInput) {
            int keyCode = e.getKeyCode();
            String keyText = KeyEvent.getKeyText((keyCode));
            if(!keyText.contains("Unbekannt")) {
                screenshotKeyBindText.setText(keyText);
                settings.setPropertyValue("screenshotKey", keyText); //andere Lösung um settings und main anzusprechen?
                main.getGlobalInputController().setScreenshotKey(settings.getPropertyValue("screenshotKey"));
                settings.saveProperties();
                changeWaitingForButtonInput();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
