package api.pixelecraft.command;

public interface ICommand {
    boolean isRegister();
    String getName();
    String getDescription();
    String getUsage();
    String getPermission();
    boolean testPermission(CommandSender sender);
    boolean executor(CommandSender sender,ICommand command,String[] args);
}
