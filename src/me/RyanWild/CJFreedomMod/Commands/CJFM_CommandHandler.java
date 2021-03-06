package me.RyanWild.CJFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CJFM_CommandHandler
{
    public static final String CJFM_COMMAND_PATH = CJFM_Command.class.getPackage().getName(); // "me.StevenLawson.TotalFreedomMod.Commands";
    public static final String CJFM_COMMAND_PREFIX = "Command_";

    public static boolean handleCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        final Player playerSender;
        final boolean senderIsConsole;

        if (sender instanceof Player)
        {
            senderIsConsole = false;
            playerSender = (Player) sender;

            TFM_Log.info(String.format("[PLAYER_COMMAND] %s (%s): /%s %s",
                    playerSender.getName(),
                    ChatColor.stripColor(playerSender.getDisplayName()),
                    commandLabel,
                    StringUtils.join(args, " ")), true);
        }
        else
        {
            senderIsConsole = true;
            playerSender = null;

            TFM_Log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s",
                    sender.getName(),
                    commandLabel,
                    StringUtils.join(args, " ")), true);
        }

        final CJFM_Command dispatcher;
        try
        {
            final ClassLoader classLoader = TotalFreedomMod.class.getClassLoader();
            dispatcher = (CJFM_Command) classLoader.loadClass(String.format("%s.%s%s",
                    CJFM_COMMAND_PATH,
                    CJFM_COMMAND_PREFIX,
                    cmd.getName().toLowerCase())).newInstance();
            dispatcher.setup(TotalFreedomMod.plugin, sender, dispatcher.getClass());
        }
        catch (Exception ex)
        {
            TFM_Log.info("Possible Command Error: " + cmd.getName());
            return true;
        }

        if (!dispatcher.senderHasPermission())
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        try
        {
            return dispatcher.run(sender, playerSender, cmd, commandLabel, args, senderIsConsole);
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Command Error: " + commandLabel);
            TFM_Log.severe(ex);
            sender.sendMessage(ChatColor.RED + "Command Error: " + ex.getMessage());
        }

        return true;
    }
}
