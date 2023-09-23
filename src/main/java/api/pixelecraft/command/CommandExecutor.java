package api.pixelecraft.command;

public interface CommandExecutor {
    boolean onCommand(CommandSender sender,ICommand command, String label, String[] args);
}
