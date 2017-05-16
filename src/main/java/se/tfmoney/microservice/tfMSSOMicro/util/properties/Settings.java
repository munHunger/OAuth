package se.tfmoney.microservice.tfMSSOMicro.util.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class for managing all settings that shouldn't reasonably be compiled.
 * i.e. username, endpoint addresses, port numbers and so on.
 * Created by Marcus Münger on 2017-04-12.
 */
public class Settings
{
    private static Properties properties = new Properties();

    static
    {
        try (InputStream input = new FileInputStream(
                Settings.class.getClassLoader().getResource("config.properties").getFile()))
        {
            properties.load(input);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Fetches an integer setting from the config.properties file.
     * If the key is not in the file or if it is not a number the defaultValue will be returned
     *
     * @param key          the key to search for
     * @param defaultValue the value to return if a value from the config cannot be returned
     *
     * @return The value in the config file if found and it is convertable to an int. defaultValue otherwise
     */
    public static int getIntSetting(String key, int defaultValue)
    {
        String value = getStringSetting(key);
        if (value != null)
        {
            try
            {
                return Integer.parseInt(getStringSetting(key));
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * Fetches a setting from the config.properties file and tries to convert it to an integer
     *
     * @param key the key to search with
     *
     * @return the setting in integer form if exists
     */
    public static int getIntSetting(String key)
    {
        return Integer.parseInt(getStringSetting(key));
    }

    /**
     * Fetches a setting from the config.properties file.
     * If the setting is not in the properties the defaultValue will be used.
     *
     * @param key          the key to search for
     * @param defaultValue the value to default to if the value cannot be found in the settings
     *
     * @return the settings value if such can be found, defaultValue otherwise
     */
    public static String getStringSetting(String key, String defaultValue)
    {
        String value = getStringSetting(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Fetches a setting from the config.properties file.
     *
     * @param key The key in the config to search for
     *
     * @return The setting if the key exists in the config, null otherwise
     */
    public static String getStringSetting(String key)
    {
        return properties.getProperty(key);
    }
}
