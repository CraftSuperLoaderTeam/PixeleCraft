package com.pixelecraft.nc.command;

import api.pixelecraft.ChatColor;
import api.pixelecraft.command.CommandSender;
import api.pixelecraft.command.ICommand;

public abstract class Command implements ICommand {
    String name, description, usage, permission;
    boolean register;

    public Command(String name) {
        this.name = name;
    }

    public void setName(String name) {
        if (register) return;
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean testPermissionSilent(CommandSender target) {
        if ((permission == null) || (permission.length() == 0)) {
            return true;
        }
        for (String p : permission.split(";")) {
            if (target.hasPermission(p)) {
                return true;
            }
        }
        return false;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public boolean executor(CommandSender sender, ICommand command, String[] args) {
        return onCommand(sender, command, name, args);
    }

    @Override
    public boolean testPermission(CommandSender target) {
        if (testPermissionSilent(target)) {
            return true;
        }
        target.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
        return false;
    }

    @Override
    public boolean isRegister() {
        return register;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    public void setup(CommandSystem system) {
        this.register = true;
        system.registerCommand(this);
    }

    public abstract boolean onCommand(CommandSender sender, ICommand command, String label, String[] args);
}
