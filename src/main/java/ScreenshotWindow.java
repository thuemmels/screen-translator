import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
  aufbauend auf
      https://stackoverflow.com/questions/40945461/java-swing-draw-rectangle-in-mouse-drag-and-drop
*/

public class ScreenshotWindow extends JPanel {
    private Main app;

    private int x;
    private int y;
    private int x2;
    private int y2;

    private int width;
    private int height;

    private BufferedImage screenshot;
    private String screenshotPath;
    private MyMouseInputAdapter mouseInputAdapter;

    private String purpose;

    ScreenshotWindow(Main app, BufferedImage image, String imagePath, String purpose) {
        this.app = app;
        screenshot = image;
        screenshotPath = imagePath;
        this.purpose = purpose;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        mouseInputAdapter = new MyMouseInputAdapter();
        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);
    }

    public void setStartPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setEndPoint(int x, int y) {
        x2 = (x);
        y2 = (y);
    }

    public void drawRectangle(Graphics g, int x, int y, int x2, int y2) {
        int rectangleX = Math.min(x,x2);
        int rectangleY = Math.min(y,y2);
        int width = Math.abs(x-x2);
        int height = Math.abs(y-y2);
        this.width = width;
        this.height = height;
        g.drawRect(rectangleX, rectangleY, width, height);
    }

    public void cropScreenshot() {
        BufferedImage croppedScreenshot = screenshot.getSubimage(x, y, width, height);
        disposeScreenshotWindow();
        app.useCroppedScreenshot(croppedScreenshot);
    }

    private void disposeScreenshotWindow() {
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void setSeparateWindowPosition(){
        app.getMainWindow().setSeparateWindowPosition(x,y);
    }

    private void setSeparateWindowSize(){
        int width = Math.abs(x-x2);
        int height = Math.abs(y-y2);
        app.getMainWindow().setSeparateWindowSize(width, height);
    }

    class MyMouseInputAdapter extends MouseInputAdapter {

        private boolean mouseWasDragged = false;
        private boolean cancelDragging = false;
        private boolean draggingDetection = true;

        public boolean isMouseWasDragged() { return mouseWasDragged; }
        public void setMouseWasDragged(boolean mouseWasDragged) { this.mouseWasDragged = mouseWasDragged; }

        public boolean isCancelDragging() { return cancelDragging; }
        public void setCancelDragging(boolean value) { cancelDragging = value; }

        public boolean isDraggingDetection() { return draggingDetection; }
        public void setDraggingDetection(boolean value) { draggingDetection = value; }

        @Override
        public void mouseClicked(MouseEvent e) {
            switch(e.getButton()){
                case MouseEvent.BUTTON1: //linksklick
                    if(purpose.equals("screenshot")){
                        try {
                            disposeScreenshotWindow(); //sollte screenshot geschlossen werden?
                            app.translateScreen(screenshotPath);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    break;
                case MouseEvent.BUTTON3: //rechtsklick cancelt dragging
                    if(isMouseWasDragged() && isDraggingDetection()) {
                        setMouseWasDragged(false);
                        setCancelDragging(true);
                        setDraggingDetection(false);
                        repaint();
                    }
                    break;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                if(!isDraggingDetection()) setDraggingDetection(true);
                setStartPoint(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e) && isDraggingDetection()) {
                setMouseWasDragged(true);
                setEndPoint(e.getX(), e.getY());
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1 && isMouseWasDragged()) {
                setEndPoint(e.getX(), e.getY());
                repaint();
                if(purpose.equals("screenshot")){
                    cropScreenshot();
                } else {
                    setSeparateWindowPosition();
                    setSeparateWindowSize();
                    disposeScreenshotWindow();
                    //Feedback, dass Position & Größe angepasst wurden
                }
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(screenshot, 0, 0, this);
        if(!mouseInputAdapter.isCancelDragging()) {
            g.setColor(Color.RED);
            drawRectangle(g, x, y, x2, y2);

        } else {
            mouseInputAdapter.setCancelDragging(false);
        }
    }
}