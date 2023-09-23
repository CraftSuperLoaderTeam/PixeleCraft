package api.pixelecraft.event.server;

import api.pixelecraft.Server;
import api.pixelecraft.command.ICommand;
import api.pixelecraft.event.Cancelable;

public class ServerCommandEvent extends ServerEvent implements Cancelable {
    ICommand command;
    Server server;
    boolean cancel;

    public ServerCommandEvent(ICommand command,Server server){
        this.command = command;
        this.server = server;
        this.cancel = false;
    }

    @Override
    public Server getServer() {
        return server;
    }

    public ICommand getCommand() {
        return command;
    }

    @Override
    public boolean isCancel() {
        return cancel;
    }

    @Override
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
