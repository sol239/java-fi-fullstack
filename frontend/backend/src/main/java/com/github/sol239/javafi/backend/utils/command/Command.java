/**
 * The package provides command utils.
 */
package com.github.sol239.javafi.backend.utils.command;

import com.github.sol239.javafi.utils.DataObject;

import java.util.List;

/**
 * Interface for commands_to_load.
 */
public interface Command {

    /**
     * Method to get the name of the command.
     * @return name of the command
     */
    String getName();

    /**
     * Method to get the description of the command.
     * @return description of the command
     */
    String getDescription();

    /**
     * Method to get the parameters of the command.
     * @return parameters of the command
     */
    String getParameters();

    /**
     * Method to run the command.
     * @param args arguments
     * @param flags flags
     * @return result
     */
    DataObject run(List<String> args, List<String> flags);
}
