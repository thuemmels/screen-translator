import org.jnativehook.GlobalScreen;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

public class Main {
    private static Main instance;
    private Settings settings;
    private InputController globalInputController;
    private MainWindow mainWindow;
    private JFrame overlappingWindow;

    private Main(){ }

    private void setUpMainWindow(){
        JFrame mainFrame = new JFrame();
        mainWindow = new MainWindow(mainFrame, settings, this);
        mainWindow.setUp();
        mainFrame.setContentPane(mainWindow);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.toFront();
    }

    private void setUpGlobalInput() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
            globalInputController = new InputController(this, settings.getPropertyValue("screenshotKey"));
            GlobalScreen.addNativeKeyListener(globalInputController);
        }
        catch(Exception e)
        {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void setUpOverlappingWindow(String translation){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        Point windowPoint = mainWindow.getSeparateWindowPosition();
        Dimension windowSize = mainWindow.getSeparateWindowSize();

        boolean useCustomizedValues = mainWindow.isPositionCustomized();

        int windowTopLeftX = useCustomizedValues ? (int) windowPoint.getX() : (int) (width/8.);
        int windowTopLeftY = useCustomizedValues ? (int) windowPoint.getY() : (int) (6 * (height/8.));
        int windowWidth = useCustomizedValues ? (int)windowSize.getWidth() : (int) (width/8.) * 6;
        int windowHeight = useCustomizedValues ? (int)windowSize.getHeight() : (int) (height/8.);

        JFrame overlappingWindow;
        JPanel contentPane;

        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        boolean isUniformTranslucencySupported =
                gd.isWindowTranslucencySupported(TRANSLUCENT);
        if(isUniformTranslucencySupported){
            JFrame.setDefaultLookAndFeelDecorated(true);
            overlappingWindow = new JFrame();
            overlappingWindow.setOpacity(0.55f);
        } else overlappingWindow = new JFrame();

        this.overlappingWindow = overlappingWindow;
        overlappingWindow.setLayout(new BorderLayout());
        overlappingWindow.setLocation(windowTopLeftX, windowTopLeftY);
        overlappingWindow.setPreferredSize(new Dimension(windowWidth,windowHeight));

        contentPane = new JPanel();
        JTextArea translationArea = new JTextArea(4,80);
        translationArea.setEditable(false);
        translationArea.setLineWrap(true);
        translationArea.setWrapStyleWord(true);
        translationArea.setText(translation);
        translationArea.setFont(translationArea.getFont().deriveFont(20f));

        overlappingWindow.add(contentPane);
        contentPane.add(translationArea);

        overlappingWindow.pack();
        overlappingWindow.setVisible(true);
        overlappingWindow.toFront();
        overlappingWindow.setAlwaysOnTop(true);
        JFrame.setDefaultLookAndFeelDecorated(false);
    }

    private void start(){
        settings = new Settings();
        TranslateText.setSupportedTargetLanguages();
        TranslateText.setTargetLanguage(settings.getPropertyValue("targetLanguage"));
        setUpGlobalInput();
        setUpMainWindow();
    }

    public void createAndShowGUI(String translatedText) {
        switch(getMainWindow().getDisplayMode()){
            case "separated":
                JFrame gui = new JFrame();
                gui.setTitle("Übersetzung");
                gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gui.setAlwaysOnTop(true);
                gui.setLocation(0,0);

                JPanel panel = new JPanel();
                JTextArea textArea = new JTextArea(20,30);
                textArea.setText(translatedText);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(textArea);

                panel.add(scrollPane);

                gui.add(panel);

                gui.pack();
                gui.setVisible(true);
                break;
            case "overlapping":
                setUpOverlappingWindow(translatedText);
                break;
        }

    }

    public static Main getInstance(){
        if(instance == null) instance = new Main();
        return instance;
    }
    public InputController getGlobalInputController(){return globalInputController;}

    public MainWindow getMainWindow(){
        if(mainWindow == null) setUpMainWindow();
        return mainWindow;
    }

    public JFrame getOverlappingWindow(){ return overlappingWindow; }

    public static void main(String[] args)
    {
        Main.getInstance().start();
    }

    public String saveImage(String name, String format, BufferedImage image) throws IOException {
        try{
            String fileName = name + "." + format;
            ImageIO.write(image, format, new File(fileName));
            return fileName;
        }
        catch (IOException ex) {
            return ex.toString();
        }
    }

    public void showScreenshot(String screenshotPath, String purpose) throws IOException {
        BufferedImage image = ImageIO.read(new File(screenshotPath));

        JFrame screenshotFrame = new JFrame();
        screenshotFrame.setContentPane(new ScreenshotWindow(this, image, screenshotPath, purpose));

        screenshotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        screenshotFrame.pack();
        screenshotFrame.setVisible(true);
        screenshotFrame.toFront();
        screenshotFrame.getContentPane().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void takeAndShowScreenshot(String purpose) throws IOException {
        if(getOverlappingWindow() != null) overlappingWindow.dispose();
        String filePath = takeScreenshot();
        showScreenshot(filePath, purpose);
    }

    /*
    https://www.codejava.net/java-se/graphics/how-to-capture-screenshot-programmatically-in-java
     */
    public String takeScreenshot() {
        String retVal = "";
        try {
            Robot robot = new Robot();
            String format = "jpg";
            String name = "FullScreenshot";

            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);

            String fileName = saveImage(name, format, screenFullImage);
            retVal = fileName;
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
        return retVal;
    }

    public void translateScreen(String filePath) throws IOException {
        System.out.println("Detecting text and translating screen...");
        String detectedText = DetectText.detectText(filePath);
        String translatedText = TranslateText.translateText(detectedText);
        createAndShowGUI(translatedText);

    }

    public void useCroppedScreenshot(BufferedImage screenshot) {
        try{
            String filePath = saveImage( "CroppedScreenshot","jpg", screenshot);
            translateScreen(filePath);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
