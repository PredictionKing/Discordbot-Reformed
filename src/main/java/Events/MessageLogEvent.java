package Events;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static Main.Main.dataSource;

public class MessageLogEvent extends ListenerAdapter {

    Logger logger = LoggerFactory.getLogger(MessageLogEvent.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);

        if (event.getMessage().getAuthor().isBot()) return;

        String messageid, message, authorid, channelid, guildid;
        messageid = event.getMessageId();
        message = event.getMessage().getContentRaw();
        authorid = event.getAuthor().getId();
        channelid = event.getChannel().getId();
        guildid = event.getGuild().getId();

        LocalDateTime date = event.getMessage().getTimeCreated().atZoneSameInstant(ZoneOffset.ofHours(2)).toLocalDateTime();

        insertIntoMessageLog(messageid, message, authorid, channelid, guildid, ActionType.CREATED, date);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        super.onMessageUpdate(event);

        if (event.getMessage().getAuthor().isBot()) return;

        String messageid, message, authorid, channelid, guildid;
        messageid = event.getMessageId();
        message = event.getMessage().getContentRaw();
        authorid = event.getAuthor().getId();
        channelid = event.getChannel().getId();
        guildid = event.getGuild().getId();

        LocalDateTime date = event.getMessage().getTimeEdited().atZoneSameInstant(ZoneOffset.ofHours(2)).toLocalDateTime();

        insertIntoMessageLog(messageid, message, authorid, channelid, guildid, ActionType.CHANGED, date);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        super.onMessageDelete(event);

        String messageid, channelid, guildid;
        messageid = event.getMessageId();
        channelid = event.getChannel().getId();
        guildid = event.getGuild().getId();


        LocalDateTime now = LocalDateTime.now();
        OffsetDateTime time = now.atOffset(ZoneOffset.ofHours(2));

        insertIntoMessageLog(messageid, null, null, channelid, guildid, ActionType.DELETED, time.toLocalDateTime());

    }

    private void insertIntoMessageLog(String messageid, String message, String authorid, String channelid, String guildid, ActionType action, LocalDateTime timestamp){

        String sql = "INSERT INTO message_log (messageid, message, authorid, channelid, guildid, action, timestamp) VALUES (?,?,?,?,?,?,?)";


        try{
            Connection conn = dataSource.getConnection();

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, messageid);
            statement.setString(2, message);
            statement.setString(3, authorid);
            statement.setString(4, channelid);
            statement.setString(5, guildid);
            statement.setString(6, action.name());
            statement.setTimestamp(7, Timestamp.valueOf(timestamp));
            statement.executeUpdate();
            conn.close();
        }catch(Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    enum ActionType {
        CREATED,
        CHANGED,
        DELETED
    }
}
