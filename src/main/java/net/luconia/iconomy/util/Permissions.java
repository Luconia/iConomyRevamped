package net.luconia.iconomy.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permissions {

	public static boolean hasPermission(CommandSender sender, String node) {
		return hasPermission(sender, node, false);
	}

	public static boolean hasPermission(CommandSender sender, String node, boolean silent) {
        if (sender instanceof Player player) {
            boolean hasPermission = player.hasPermission(node);
            if (!hasPermission && !silent)
                Messaging.sendErrorMessage(player, "You do not have the permission to use that command.");
            return hasPermission;
        }
        return true;
	}
}
