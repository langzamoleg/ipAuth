package tech.pasha.ipauth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Objects;

public class EventListener implements Listener {
    private static Connection connection;
    public static void Conn() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:ipAuth.s3db");
    }

    public static void closeConn() throws SQLException {
        connection.close();
    }

    public static void CreateDB() throws ClassNotFoundException, SQLException
    {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'users' ('uuid' text, 'nickname' text, 'ip' text);");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT ip FROM users WHERE nickname = ?;");
        preparedStatement.setString(1, event.getPlayer().getName());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.getString("ip") == null) {
            PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO users (uuid, nickname, ip) VALUES (?, ?, ?);");
            preparedStatement1.setString(1, event.getPlayer().getUniqueId().toString());
            preparedStatement1.setString(2, event.getPlayer().getName());
            preparedStatement1.setString(3, event.getPlayer().getAddress().getAddress().toString());
            preparedStatement1.execute();
            return;
        }
        if (!Objects.equals(resultSet.getString("ip"), event.getPlayer().getAddress().getAddress().toString())) {
            PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM users WHERE ip = ?;");
            preparedStatement1.setString(1, event.getPlayer().getAddress().getAddress().toString());
            preparedStatement1.execute();
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format("ban-ip %s banned by ipAuth: logged from other ip", event.getPlayer().getName()));
            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                if (all.isOp()) {
                    all.sendMessage(ChatColor.DARK_RED + String.format("WARNING: someone tried to log as %s, ip %s was banned", event.getPlayer().getName(), event.getPlayer().getAddress().getAddress().toString()));
                }
            }
            return;
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }
}
