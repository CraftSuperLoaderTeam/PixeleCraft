package io.github.csl.logging;

import org.apache.commons.lang3.Validate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger {
    String name;
    SimpleDateFormat format;
    private Logger(String name){
        this.name = name;
        this.format = new SimpleDateFormat("HH:mm:ss");
    }

    public static Logger getLogger(String name){
        return new Logger(name);
    }

    public static Logger getLogger(Class<?> clazz){
        return new Logger(clazz.getSimpleName());
    }

    public void info(String message){
        log(Level.INFO,message);
    }

    public void warn(String message){
        log(Level.WARNING,message);
    }

    public void error(String message){
        log(Level.SEVERE,message);
    }
    public void error(String message,Throwable throwable){
        log(Level.SEVERE,message,throwable);
    }

    public void log(Level level,String message,Object... object){
        if(message == null) message = "null";
        Validate.notNull(level,"Logger level not null");
        if(LogManager.enable){
            String f = LogManager.formatAnsi(message);
            if (level.equals(Level.INFO)) {
                LogManager.sys_out.println("["+format.format(new Date())+"] ["+Thread.currentThread().getName()+"/INFO-"+name+"]: "+f);
            }
            if (level.equals(Level.WARNING)) {
                LogManager.sys_out.println("\u001b[33m["+format.format(new Date())+"] ["+Thread.currentThread().getName()+"/WARN-"+name+"]: "+f+"\u001b[0m");
            }
            if (level.equals(Level.SEVERE)) {
                LogManager.sys_err.println("[\u001b[91m["+format.format(new Date())+"] ["+Thread.currentThread().getName()+"/ERROR-"+name+"]: "+f+"\u001b[0m");
            }
        }
    }

    public void log(Level level,String message,Throwable throwable){
        if(message == null) message = "null";
        Validate.notNull(level,"Logger level not null");
        Validate.notNull(throwable,"Logger throwable not null");
        if(LogManager.enable){
            String f = LogManager.formatAnsi(message);
            if (level.equals(Level.INFO)) {
                LogManager.sys_out.println("["+format.format(new Date())+"] ["+Thread.currentThread().getName()+"/INFO-"+name+"]: "+f);
            }
            if (level.equals(Level.WARNING)) {
                LogManager.sys_out.println("\u001b[33m["+format.format(new Date())+"] ["+Thread.currentThread().getName()+"/WARN-"+name+"]: "+f+"\u001b[0m");
            }
            if (level.equals(Level.SEVERE)) {
                LogManager.sys_err.println("[\u001b[91m["+format.format(new Date())+"] ["+Thread.currentThread().getName()+"/ERROR-"+name+"]: "+f+"\u001b[0m");
            }
            throwable.printStackTrace(LogManager.sys_err);
        }
    }
}
