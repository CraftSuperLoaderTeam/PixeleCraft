package api.pixelecraft.event.server;

import api.pixelecraft.Server;

import java.net.InetSocketAddress;

public class ServerPingEvent extends ServerEvent{
    InetSocketAddress address;
    String motd;
    Server server;
    int maxplayers;

    public ServerPingEvent(InetSocketAddress address,String motd,final int maxplayers,Server server){
        this.address = address;
        this.motd = motd;
        this.maxplayers = maxplayers;
        this.server = server;
    }

    public void setMaxplayers(int maxplayers) {
        this.maxplayers = maxplayers;
    }

    public int getMaxplayers() {
        return maxplayers;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getMotd() {
        return motd;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    Server getServer() {
        return server;
    }
}
