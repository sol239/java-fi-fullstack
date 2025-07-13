package com.github.sol239.javafi.backend.utils.command.Commands;

import com.github.sol239.javafi.utils.files.ConfigHandler;
import com.github.sol239.javafi.utils.DataObject;
import com.github.sol239.javafi.utils.command.Command;
import com.google.auto.service.AutoService;

import java.util.List;

@Deprecated
/**
 * A command to configure the database connection.
 */
@AutoService(Command.class)
public class ConfigCommand implements Command {
    /**
     * Method to get the name of the command.
     *
     * @return name of the command
     */
    @Override
    public String getName() {
        return "config";
    }

    /**
     * Method to get the description of the command.
     *
     * @return description of the command
     */
    @Override
    public String getDescription() {
        return "Usage: config [OPTION]...\n" +
                "The command displays the current configuration.\n" +
                getParameters();
    }

    /**
     * Method to get the parameters of the command.
     *
     * @return parameters of the command
     */
    @Override
    public String getParameters() {
        return "Options:\n" +
                "  -h, --help\n" +
                "  -u=URL, --url=URL\n" +
                "  -p=PASSWORD, --password=PASSWORD\n" +
                "  -n=USERNAME, --username=USERNAME";
    }

    /**
     * Method to run the command.
     *
     * @param args  arguments
     * @param flags flags
     * @return result
     */
    @Override
    public DataObject run(List<String> args, List<String> flags) {

        ConfigHandler cfg = new ConfigHandler();
        cfg.loadConfigMap(ConfigHandler.CONFIG_FILE);

        for (String flag : flags) {
            if (flag.equals("-h") || flag.equals("--help")) {
                return new DataObject(200, "server", getDescription());
            } else if (flag.startsWith("-u=") || flag.startsWith("--url=")) {
                cfg.url = flag.substring(flag.indexOf("=") + 1);
                cfg.configMap.put("url", cfg.url);
            } else if (flag.startsWith("-p=") || flag.startsWith("--password=")) {
                cfg.password = flag.substring(flag.indexOf("=") + 1);
                cfg.configMap.put("password", cfg.password);
            } else if (flag.startsWith("-n=") || flag.startsWith("--username=")) {
                cfg.username = flag.substring(flag.indexOf("=") + 1);
                cfg.configMap.put("username", cfg.username);
            }
        }
        cfg.writeConfigMap(ConfigHandler.CONFIG_FILE);
        return new DataObject(200, "server", cfg.toString());

    }
}
