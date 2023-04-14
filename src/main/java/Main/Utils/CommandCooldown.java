package Main.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static Main.Main.dataSource;

public class CommandCooldown {
    public static void triggerCommandCooldown(String discordid, String guildid, String commandName, Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = format.format(date);

            String SQL = "INSERT INTO command_cooldowns (discordid, guildid, commandname, commandtime) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE commandTime=?";
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, discordid);
            preparedStatement.setString(2, guildid);
            preparedStatement.setString(3, commandName);
            preparedStatement.setString(4, currentDateTime);
            preparedStatement.setString(5, currentDateTime);
            preparedStatement.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String checkCommandCooldown(String discordid, String commandName) {
        try {
            String SQL = "SELECT * FROM command_cooldowns WHERE discordid=? AND commandname=?";
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            preparedStatement.setString(1, discordid);
            preparedStatement.setString(2, commandName);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String commandTime = rs.getString("commandTime");
                return commandTime;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);//minus number would decrement the hours
        return cal.getTime();
    }
}
