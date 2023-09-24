package com.pixelecraft.nc.command.minecraft;

import api.pixelecraft.ChatColor;
import api.pixelecraft.command.CommandSender;
import api.pixelecraft.command.ICommand;
import com.pixelecraft.nc.MetaData;
import com.pixelecraft.nc.command.Command;
import com.pixelecraft.nc.command.CommandSystem;

public class HelpCommand extends Command {

    CommandSystem system;

    public HelpCommand(CommandSystem system) {
        super("help");
        this.setDescription("A Minecraft command.");
        this.setUsage("/help [page]");
        this.system = system;
    }

    @Override
    public boolean onCommand(CommandSender sender, ICommand command, String label, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.DARK_AQUA)
                .append("==["+ MetaData.servername+"|Command Helper]==")
                .append("\n")
                .append(ChatColor.RESET);

        for(ICommand command1: system.getRegisteredCommands()){
            sb.append(ChatColor.GOLD + "/")
                    .append(command1.getName())
                    .append(ChatColor.DARK_AQUA)
                    .append(" Usage:")
                    .append(command1.getUsage())
                    .append(ChatColor.GREEN)
                    .append(" -")
                    .append(command1.getDescription())
                    .append('\n')
                    .append(ChatColor.RESET);
        }

        sender.sendMessage(sb.toString());
        return true;
    }
}
