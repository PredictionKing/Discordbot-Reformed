package Main.Utils;

import Main.Utils.Enums.StatNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Main.Main.dataSource;

public class CharacterStatsUtils {

    static Logger logger = LoggerFactory.getLogger(CharacterStatsUtils.class);

    public static double getCharacterStats(StatNames statname, String discordid, String guildid){
        String sql = "SELECT "+statname+" FROM character_stats WHERE discordid=? AND guildid=?";
        try{
            Connection conn = dataSource.getConnection();
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setString(1,discordid);
            prep.setString(2,guildid);
            ResultSet rs = prep.executeQuery();
            while(rs.next()){
                Double r = rs.getDouble(1);
                return r;
            }
        }catch (SQLException e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public static void addToCharacterStats(StatNames statname, String discordid, String guildid, double amount){
        String sql = "UPDATE character_stats SET "+statname+"="+statname+"+? WHERE discordid=? AND guildid=?";
        try{
            Connection conn = dataSource.getConnection();
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setDouble(1,amount);
            prep.setString(2,discordid);
            prep.setString(3,guildid);
            prep.executeUpdate();
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}

