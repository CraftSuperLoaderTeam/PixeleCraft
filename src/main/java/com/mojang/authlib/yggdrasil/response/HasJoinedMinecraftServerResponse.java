package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;

import java.util.UUID;

public class HasJoinedMinecraftServerResponse extends Response {
    private UUID id;
    private PropertyMap properties;

    public UUID getId() {
        return id;
    }

    public PropertyMap getProperties() {
        return properties;
    }
}
