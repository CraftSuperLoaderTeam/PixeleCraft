package com.pixelecraft.nc.util;

import com.google.gson.*;
import com.pixelecraft.nc.util.text.ChatComponentSelector;
import com.pixelecraft.nc.util.text.ChatComponentString;
import com.pixelecraft.nc.util.text.ChatComponentTranslation;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public interface IChatComponent extends Iterable<IChatComponent> {


    IChatComponent setChatStyle(ChatStyle style);

    ChatStyle getChatStyle();

    IChatComponent appendText(String text);

    IChatComponent appendSibling(IChatComponent component);

    String getUnformattedComponentText();

    String getUnformattedText();

    String getFormattedText();

    List<IChatComponent> getSiblings();

    IChatComponent createCopy();

    public static class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {
        private static final Gson GSON;

        public IChatComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonPrimitive()) {
                return new ChatComponentString(p_deserialize_1_.getAsString());
            } else if (!p_deserialize_1_.isJsonObject()) {
                if (p_deserialize_1_.isJsonArray()) {
                    JsonArray jsonarray1 = p_deserialize_1_.getAsJsonArray();
                    IChatComponent ichatcomponent1 = null;

                    for (JsonElement jsonelement : jsonarray1) {
                        IChatComponent ichatcomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);

                        if (ichatcomponent1 == null) {
                            ichatcomponent1 = ichatcomponent2;
                        } else {
                            ichatcomponent1.appendSibling(ichatcomponent2);
                        }
                    }

                    return ichatcomponent1;
                } else {
                    throw new JsonParseException("Don\'t know how to turn " + p_deserialize_1_.toString() + " into a Component");
                }
            } else {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                IChatComponent ichatcomponent;

                if (jsonobject.has("text")) {
                    ichatcomponent = new ChatComponentString(jsonobject.get("text").getAsString());
                } else if (jsonobject.has("translate")) {
                    String s = jsonobject.get("translate").getAsString();

                    if (jsonobject.has("with")) {
                        JsonArray jsonarray = jsonobject.getAsJsonArray("with");
                        Object[] aobject = new Object[jsonarray.size()];

                        for (int i = 0; i < aobject.length; ++i) {
                            aobject[i] = this.deserialize(jsonarray.get(i), p_deserialize_2_, p_deserialize_3_);

                            if (aobject[i] instanceof ChatComponentString) {
                                ChatComponentString chatcomponenttext = (ChatComponentString) aobject[i];

                                if (chatcomponenttext.getChatStyle().isEmpty() && chatcomponenttext.getSiblings().isEmpty()) {
                                    aobject[i] = chatcomponenttext.getChatComponentText_TextValue();
                                }
                            }
                        }

                        ichatcomponent = new ChatComponentTranslation(s, aobject);
                    } else {
                        ichatcomponent = new ChatComponentTranslation(s, new Object[0]);
                    }
                } else {
                    if (!jsonobject.has("selector")) {
                        throw new JsonParseException("Don\'t know how to turn " + p_deserialize_1_.toString() + " into a Component");
                    }

                    ichatcomponent = new ChatComponentSelector(JsonUtils.getString(jsonobject, "selector"));
                }

                if (jsonobject.has("extra")) {
                    JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");

                    if (jsonarray2.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (int j = 0; j < jsonarray2.size(); ++j) {
                        ichatcomponent.appendSibling(this.deserialize(jsonarray2.get(j), p_deserialize_2_, p_deserialize_3_));
                    }
                }

                ichatcomponent.setChatStyle((ChatStyle) p_deserialize_3_.deserialize(p_deserialize_1_, ChatStyle.class));
                return ichatcomponent;
            }
        }

        private void serializeChatStyle(ChatStyle style, JsonObject object, JsonSerializationContext ctx) {
            JsonElement jsonelement = ctx.serialize(style);

            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = (JsonObject) jsonelement;

                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    object.add((String) entry.getKey(), (JsonElement) entry.getValue());
                }
            }
        }

        public JsonElement serialize(IChatComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            if (p_serialize_1_ instanceof ChatComponentString && p_serialize_1_.getChatStyle().isEmpty() && p_serialize_1_.getSiblings().isEmpty()) {
                return new JsonPrimitive(((ChatComponentString) p_serialize_1_).getChatComponentText_TextValue());
            } else {
                JsonObject jsonobject = new JsonObject();

                if (!p_serialize_1_.getChatStyle().isEmpty()) {
                    this.serializeChatStyle(p_serialize_1_.getChatStyle(), jsonobject, p_serialize_3_);
                }

                if (!p_serialize_1_.getSiblings().isEmpty()) {
                    JsonArray jsonarray = new JsonArray();

                    for (IChatComponent ichatcomponent : p_serialize_1_.getSiblings()) {
                        jsonarray.add(this.serialize((IChatComponent) ichatcomponent, ichatcomponent.getClass(), p_serialize_3_));
                    }

                    jsonobject.add("extra", jsonarray);
                }

                return jsonobject;
            }
        }

        public static String componentToJson(IChatComponent component) {
            return GSON.toJson((Object) component);
        }

        public static IChatComponent jsonToComponent(String json) {
            return (IChatComponent) GSON.fromJson(json, IChatComponent.class);
        }

        static {
            GsonBuilder gsonbuilder = new GsonBuilder();
            gsonbuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
            gsonbuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
            gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
            GSON = gsonbuilder.create();
        }
    }
}
