package Main.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Main.Main.dataSource;

public class CoinUtils {

    public static Double getCoinsFromUser(String discordId, String guildid) {
        try {
            String sql = "SELECT coins FROM character_stats WHERE discordid=? AND guildid=?";
            Connection conn = dataSource.getConnection();
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setString(1, discordId);
            prep.setString(2, guildid);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                double coins = rs.getDouble("coins");
                return coins;
            }
            prep.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static void changeCoinsFromUser(String discordId, String guildid, double amount) {
        try {
            String selectsql = "SELECT id FROM character_stats WHERE discordid=? AND guildid=?";
            String sqlupdate = "UPDATE character_stats SET coins=coins+? WHERE discordid=? AND guildid=?";
            String sqlinsert = "INSERT INTO character_stats (discordid, coins, guildid) VALUES (?,?,?)";
            Connection conn = dataSource.getConnection();
            PreparedStatement prep = conn.prepareStatement(selectsql);
            prep.setString(1,discordId);
            prep.setString(2,guildid);
            ResultSet rs = prep.executeQuery();
            if(!rs.next()){
                prep = conn.prepareStatement(sqlinsert);
                prep.setString(1, discordId);
                prep.setDouble(2, 50);
                prep.setString(3, guildid);
                prep.executeUpdate();
                prep.close();
            }else{
                prep = conn.prepareStatement(sqlupdate);
                prep.setDouble(1,amount);
                prep.setString(2, discordId);
                prep.setString(3, guildid);
                prep.executeUpdate();
                prep.close();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
