package com.pixelecraft.nc.network.packet.handle;

import com.pixelecraft.nc.util.IChatComponent;

public interface NetHandle {
    void onDisconnect(IChatComponent reason);
}
