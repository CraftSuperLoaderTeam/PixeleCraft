package com.pixelecraft.nc.command.minecraft;

import api.pixelecraft.ChatColor;
import api.pixelecraft.Piexele;
import api.pixelecraft.command.CommandSender;
import api.pixelecraft.command.ICommand;
import com.pixelecraft.nc.command.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
        this.setDescription("Stop this server.");
        this.setUsage("/stop");
        this.setPermission("nc.command.stop");
    }

    @Override
    public boolean onCommand(CommandSender sender, ICommand command, String label, String[] args) {
        if(!this.testPermission(sender)) return false;
        sender.sendMessage(ChatColor.GOLD+"Command stop server.");
        System.exit(0);
        return false;
    }
}
