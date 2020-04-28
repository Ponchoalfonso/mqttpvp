package io.github.ponchoalfonso.mqtt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * <h1>Class CommandMQTT</h1>
 * A class that defines the command: /mqtt<br>
 * This command displays a simple friendly message to the player that uses the command.
 *
 * @author Alfonso Valencia
 * @version 1.0
 * @see org.bukkit.command.CommandExecutor
 */
public class CommandMQTT implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0)
                return  false;
            Player player = (Player) sender;

            player.sendMessage("Indeed one of the server's plugin uses MQTT!");
        }
        return true;
    }
}
