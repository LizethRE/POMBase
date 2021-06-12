package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {
    private Properties properties;

    public Properties initializeProperties(){
        properties = new Properties();

        try {
            InputStream inputStream = Property.class.getResourceAsStream("../parameters.properties");
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
