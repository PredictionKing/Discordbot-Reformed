package Events;

import Main.Utils.InitDatabase;
import com.mysql.cj.jdbc.MysqlDataSource;
import commands.games.FarmCommand;
import commands.games.SlotsCommand;
import commands.utilities.DeleteCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static Main.Main.dataSource;

public class ReadyEvent implements EventListener {

    private JDA jda;
    private Logger logger = LoggerFactory.getLogger(ReadyEvent.class);

    @Override
    public void onEvent(GenericEvent event) {
        if(event instanceof net.dv8tion.jda.api.events.session.ReadyEvent) {
            logger.info("Ready Event fired!");
            initSlashCommands();

            dataSource = new MysqlDataSource();
            dataSource.setUser(System.getenv("db_username"));
            dataSource.setPassword(System.getenv("db_password"));
            dataSource.setServerName(System.getenv("db_host"));
            dataSource.setDatabaseName("discordbot-reformed-dev");

            new InitDatabase();
            this.jda.addEventListener(new MessageLogEvent());
            this.jda.addEventListener(new SlotsCommand());
            this.jda.addEventListener(new DeleteCommand());
            this.jda.addEventListener(new FarmCommand());
        }

    }

    public ReadyEvent(JDA jda) {
        this.jda = jda;
    }

    private void initSlashCommands(){
        //Command data is defined here
        CommandData delete = Commands.slash("delete","Deletes a given ammount of messages in this channel.")
                .addOptions(new OptionData(OptionType.INTEGER,"lines", "How many lines should be deleted starting from the latest message"));
        CommandData slots = Commands.slash("slots", "This is a slotgame")
                .addOptions(new OptionData(OptionType.INTEGER, "bet-slots", "This is the amount you want to bet", true));
        CommandData farm = Commands.slash("farm", "Farm items for your character")
                .addOptions(new OptionData(OptionType.STRING, "farming-type", "Select how you want to farm", true)
                        .addChoice("Chopping Wood","Chopping Wood")
                        .addChoice("Mining","Mining")
                        .addChoice("Fishing","Fishing"));
        for(Guild g:jda.getGuilds()){
            CommandListUpdateAction guildCommands = g.updateCommands();
            guildCommands.addCommands(delete,slots,farm);
            guildCommands.queue();
            logger.info(String.format("SlashCommands for %s were added",g.getName()));
        }
    }
}
