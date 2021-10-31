import org.apache.commons.io.FileUtils;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Settings {
    private Properties props;
    private String propertiesDirectory;
    private String propertiesPath;

    public Settings(){
        setUpProperties();
    }

    private void setUpProperties() {
        try {
            props = new Properties();
            propertiesDirectory = System.getProperty("user.home") + File.separator + "Screen Translator";
            propertiesPath = System.getProperty("user.home") + File.separator + "Screen Translator" + File.separator + "config.properties";

            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("config.properties");

            if(!Files.isDirectory(Paths.get(propertiesDirectory))) {
                Files.createDirectories(Paths.get(propertiesDirectory));
                FileUtils.copyInputStreamToFile(inputStream, new File(propertiesPath));
            }
            else if(!Files.exists(Paths.get(propertiesPath))) {
                FileUtils.copyInputStreamToFile(inputStream, new File(propertiesPath));
            }
            inputStream = new FileInputStream(propertiesPath);
            props.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPropertyValue(String property) {
        return props.getProperty(property);
    } //ENUM STATT STRING

    public void saveProperties(){
        try {
            props.store(new FileOutputStream(propertiesPath), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPropertyValue(String property, String value) {
        props.setProperty(property, value);
    }
}
