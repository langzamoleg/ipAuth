package tech.pasha.ipauth;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class IpAuth extends JavaPlugin {
    private static final EventListener listener = new EventListener();
    @Override
    public void onEnable() {
        try {
            EventListener.Conn();
            EventListener.CreateDB();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getPluginManager().registerEvents(listener, this);

    }

    @Override
    public void onDisable() {
        try {
            EventListener.closeConn();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
