package com.pixelecraft.nc;

import com.pixelecraft.nc.craft.CraftServer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        OptionParser parser = new OptionParser(){
            {
                acceptsAll(asList("p", "port", "server-port"), "Port to listen on")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Port");

                acceptsAll(asList("o", "online-mode"), "Whether to use online authentication")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .describedAs("Authentication");

                acceptsAll(asList("s", "size", "max-players"), "Maximum amount of players")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Server size");

                acceptsAll(asList("v", "version"), "Show the CraftBukkit Version");

                acceptsAll(asList("nojline"), "Disables jline and emulates the vanilla console");
            }
        };

        int v = getJavaVersion();
        if(v < 8){
            System.err.println("[ERROR]: Minecraft "+MetaData.minecraft_version+" requires running the server with Java 8.");
            System.err.println("[ERROR]: Download Java 8 (or above) from https://adoptium.net/");
        }

        System.out.println(getVersion());
        System.out.println("OS Version: "+System.getProperty("os.name")+" "+System.getProperty("os.arch"));
        System.out.println("Launching server...");

        OptionSet set = parser.parse(args);
        if(set.has("v")){
            System.out.println("["+MetaData.servername+"]: Server core version '"+MetaData.version+"'.");
            System.out.println("["+MetaData.servername+"]: Minecraft version '"+MetaData.minecraft_version+"'.");
        }else CraftServer.main(set);
    }

    private static List<String> asList(String... params) {
        return Arrays.asList(params);
    }

    public static String getVersion(){
        return "Java " + System.getProperty("java.version") + '(' + System.getProperty("java.vm.name") + ')';
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.specification.version");
        String[] parts = version.split("\\.");
        String errorMsg = "Could not determine version of the current JVM";
        if (parts.length == 0) {
            throw new IllegalStateException("Could not determine version of the current JVM");
        } else if (parts[0].equals("1")) {
            if (parts.length < 2) {
                throw new IllegalStateException("Could not determine version of the current JVM");
            } else {
                return Integer.parseInt(parts[1]);
            }
        } else {
            return Integer.parseInt(parts[0]);
        }
    }
}