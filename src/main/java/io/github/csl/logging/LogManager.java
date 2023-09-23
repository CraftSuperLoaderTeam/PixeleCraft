package io.github.csl.logging;

import api.pixelecraft.ChatColor;

import java.io.*;

public class LogManager implements Runnable{
    static PrintStream sys_out,sys_err;
    static BufferedReader reader;
    static boolean enable = false;

    public static void init(){
        LogManager.sys_out = System.out;
        LogManager.sys_err = System.err;

        PipedInputStream pipIn = new PipedInputStream();
        PipedOutputStream pipOut = new PipedOutputStream();

        try{
            pipOut.connect(pipIn);
            enable = true;
        }catch (IOException e){
            enable = false;
            LogManager.sys_err.println("Cannot init NettyCraftLoggerSystem.");
            e.printStackTrace(LogManager.sys_err);
        }

        if(enable){
            reader = new BufferedReader(new InputStreamReader(pipIn));
            new Thread(new LogManager()).start();
        }
    }

    public static String formatAnsi(String format){
        return format
                .replaceAll(ChatColor.DARK_BLUE.toString(),"\u001b[34m")
                .replaceAll(ChatColor.DARK_GREEN.toString(),"\u001b[32m")
                .replaceAll(ChatColor.DARK_AQUA.toString(),"\u001b[36m")
                .replaceAll(ChatColor.DARK_RED.toString(),"\u001b[31m")
                .replaceAll(ChatColor.DARK_PURPLE.toString(),"\u001b[35m")
                .replaceAll(ChatColor.GOLD.toString(),"\u001b[33m")
                .replaceAll(ChatColor.GRAY.toString(),"\u001b[90m")
                .replaceAll(ChatColor.DARK_GRAY.toString(),"\u001b[30m")
                .replaceAll(ChatColor.BLUE.toString(),"\u001b[94m")
                .replaceAll(ChatColor.GREEN.toString(), "\u001b[92m")
                .replaceAll(ChatColor.AQUA.toString(),"\u001b[96m")
                .replaceAll(ChatColor.RED.toString(),"\u001b[91m")
                .replaceAll(ChatColor.LIGHT_PURPLE.toString(), "\u001b[95m")
                .replaceAll(ChatColor.YELLOW.toString(), "\u001b[93m")
                .replaceAll(ChatColor.WHITE.toString(), "\u001b[97m")
                .replaceAll(ChatColor.UNDERLINE.toString(), "\n")
                .replaceAll(ChatColor.RESET.toString(),"\u001b[0m")
                .replaceAll(ChatColor.MAGIC.toString(), "")
                .replaceAll(ChatColor.BLUE.toString(), "")
                .replaceAll(ChatColor.STRIKETHROUGH.toString(),"")
                .replaceAll(ChatColor.ITALIC.toString(), "");
    }

    @Override
    public void run() {
        String s;
        try{
            while ((s = reader.readLine())!=null){
                LogManager.sys_out.println("[STD]: "+formatAnsi(s));
            }
        }catch (IOException io){
        }
    }
}
