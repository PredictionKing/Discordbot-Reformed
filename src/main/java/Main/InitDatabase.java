package Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static Main.Main.dataSource;

public class InitDatabase {

    Logger logger = LoggerFactory.getLogger(InitDatabase.class);

    public InitDatabase(){
        Start();
    }

    public void Start(){
        String logging_event = "CREATE TABLE IF NOT EXISTS `logging_event` (\n" +
                "    timestmp         BIGINT NOT NULL,\n" +
                "    formatted_message  TEXT NOT NULL,\n" +
                "    logger_name       VARCHAR(254) NOT NULL,\n" +
                "    level_string      VARCHAR(254) NOT NULL,\n" +
                "    thread_name       VARCHAR(254),\n" +
                "    reference_flag    SMALLINT,\n" +
                "    arg0              VARCHAR(254),\n" +
                "    arg1              VARCHAR(254),\n" +
                "    arg2              VARCHAR(254),\n" +
                "    arg3              VARCHAR(254),\n" +
                "    caller_filename   VARCHAR(254) NOT NULL,\n" +
                "    caller_class      VARCHAR(254) NOT NULL,\n" +
                "    caller_method     VARCHAR(254) NOT NULL,\n" +
                "    caller_line       CHAR(4) NOT NULL,\n" +
                "    event_id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY\n" +
                "  );";

        String logging_event_exception = "CREATE TABLE IF NOT EXISTS `logging_event_exception` (\n" +
                "    event_id\t      BIGINT NOT NULL,\n" +
                "    mapped_key        VARCHAR(254) NOT NULL,\n" +
                "    mapped_value      TEXT,\n" +
                "    PRIMARY KEY(event_id, mapped_key),\n" +
                "    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)\n" +
                "  );";

        String logging_event_property = "CREATE TABLE IF NOT EXISTS `logging_event_property` (\n" +
                "    event_id         BIGINT NOT NULL,\n" +
                "    i                SMALLINT NOT NULL,\n" +
                "    trace_line       VARCHAR(254) NOT NULL,\n" +
                "    PRIMARY KEY(event_id, i),\n" +
                "    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)\n" +
                "  );";

        String message_log = ""
                + "CREATE TABLE IF NOT EXISTS `message_log` ( "
                + "                     id        INT NOT NULL auto_increment, "
                + "                    messageid VARCHAR(70) , "
                + "                    message   TEXT NULL, "
                + "                    authorid  VARCHAR(70) NULL, "
                + "                    channelid  VARCHAR(70) NOT NULL, "
                + "                    guildid   VARCHAR(70) , "
                + "                    action    ENUM('CREATED', 'CHANGED', 'DELETED', '') , "
                + "                    timestamp DATETIME , "
                + "                    PRIMARY KEY (id) "
                + "                  ) ENGINE = InnoDB;";

        String guild_info = "CREATE TABLE IF NOT EXISTS `guild_info` (\n" +
                "     guildid          VARCHAR(70),\n" +
                "     guildname        VARCHAR(70),\n" +
                "     guilddescription TEXT NULL,\n" +
                "     guildiconurl     VARCHAR(70) NULL,\n" +
                "     guildownerid     VARCHAR(70),\n" +
                "     PRIMARY KEY (guildid)\n" +
                ") ENGINE = InnoDB;";

        String user_info = "CREATE TABLE IF NOT EXISTS `user_info` (\n" +
                "     discordid     VARCHAR(70),\n" +
                "     name          VARCHAR(70),\n" +
                "     discriminator TEXT,\n" +
                "     avatarurl     VARCHAR(70) NULL,\n" +
                "     PRIMARY KEY (discordid)\n" +
                ") ENGINE = InnoDB;";

        String member_info = " CREATE TABLE IF NOT EXISTS `member_info` (\n" +
                "                          id         int NOT NULL auto_increment ,\n" +
                "                          discordid  VARCHAR(70) ,\n" +
                "                          guildid    VARCHAR(70) ,\n" +
                "                          isboosting boolean ,\n" +
                "                          timejoined timestamp NULL,\n" +
                "                          PRIMARY KEY (id)) ENGINE = InnoDB;";

        try{
            Connection conn = dataSource.getConnection();

            conn.prepareStatement(logging_event).executeUpdate();
            conn.prepareStatement(logging_event_exception).executeUpdate();
            conn.prepareStatement(logging_event_property).executeUpdate();
            conn.prepareStatement(message_log).executeUpdate();
            conn.prepareStatement(guild_info).executeUpdate();
            conn.prepareStatement(user_info).executeUpdate();
            conn.prepareStatement(member_info).executeUpdate();

            conn.close();
        }catch(Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
