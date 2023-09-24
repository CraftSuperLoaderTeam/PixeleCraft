package com.pixelecraft.nc.command;

import api.pixelecraft.ChatColor;
import api.pixelecraft.Piexele;
import api.pixelecraft.command.CommandSender;
import api.pixelecraft.command.ICommand;
import api.pixelecraft.event.server.ServerCommandEvent;
import com.pixelecraft.nc.craft.CraftServer;
import io.github.csl.logging.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class CommandSystem {
    private static final Logger log = Logger.getLogger(CommandSystem.class);
    CraftServer server;
    List<ICommand> commands = new CopyOnWriteArrayList<>();

    public CommandSystem(CraftServer server){
        this.server = server;
    }

    public void senderCommand(CommandSender sender,String command){

        String c = command.trim();
        if(c.isEmpty()) return;

        String[] array = c.split(" ");
        String label = array[0];
        String[] args = new String[array.length - 1];

        if(array.length != 1){
            System.arraycopy(array,1,args,0,args.length);
        }

        try {
            for (ICommand f : commands) {
                if (f.getName().equals(label)) {

                    if(sender instanceof ConsoleSender){
                        ServerCommandEvent event = new ServerCommandEvent(f,server);
                        server.getPluginManager().callEvent(event);
                        if(event.isCancel()) return;
                        f = event.getCommand();
                    }

                    f.executor(sender,f,args);
                    return;
                }
            }
            if(sender instanceof ConsoleSender){
                log.log(Level.WARNING,ChatColor.RED+"Unknown command '"+label+"'. Please type 'help'.");
                return;
            }
            sender.sendMessage(ChatColor.RED+"Unknown command '"+label+"'. Please type 'help'.");
        }catch (Exception e){
            log.log(Level.SEVERE,"CommandException: "+e.getLocalizedMessage(),e);
        }
    }

    public List<ICommand> getRegisteredCommands(){
        return commands;
    }

    public void issueCommand(String command){
       senderCommand(Piexele.getConsoleSender(),command);
    }

    public void registerCommand(ICommand command){
        if(command == null){
            log.log(Level.SEVERE,"IllegalArgumentException: registerCommand(ICommand) is null.");
            return;
        }
        if(command.isRegister()) {
            log.log(Level.SEVERE,"Command [/"+command.getName()+"] has already been registered.");
            return;
        }
        commands.add(command);
    }
}
