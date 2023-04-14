package commands.games;

import Main.Utils.Enums.StatNames;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static Main.Utils.CharacterStatsUtils.addToCharacterStats;
import static Main.Utils.CharacterStatsUtils.getCharacterStats;
import static Main.Utils.CommandCooldown.*;

public class FarmCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (!event.getName().equalsIgnoreCase("farm")) return;

        String farmType = event.getOption("farming-type").getAsString();
        String discordid = event.getUser().getId();
        String guildid = event.getGuild().getId();

        String commandTime = checkCommandCooldown(discordid, "farm");
        Date currentDate = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin")).getTime();

        if (commandTime == null) {
            triggerCommandCooldown(discordid, guildid, "farm", currentDate);
            event.reply(farm(farmType, discordid, guildid)).queue();
        }else{
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));

                Date formattedCommandTime = formatter.parse(commandTime);
                formattedCommandTime = addHours(formattedCommandTime, 6);
                if (currentDate.after(formattedCommandTime)) {
                    triggerCommandCooldown(discordid, guildid, "farm", currentDate);
                    event.reply(farm(farmType, discordid, guildid)).queue();
                } else {
                    SimpleDateFormat newFormat = new SimpleDateFormat("dd.M.yyyy HH:mm:ss");
                    newFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                    String gerFormattedCommandTime = newFormat.format(formattedCommandTime);

                    event.reply(String.format("\uD83D\uDE35 Sorry, you have to wait until %s to use that command again. \uD83D\uDE35", gerFormattedCommandTime))
                            .setEphemeral(true)
                            .queue((msg) -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
                }

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public String farm(String farmType,String discordid, String guildid){
        int amount = 0;
        String itemtype = "";
        if (farmType.equalsIgnoreCase("chopping wood")) farmType = "lumberjack";
        double multiplier = getCharacterStats(StatNames.valueOf(farmType.toUpperCase()),discordid, guildid);
        if (farmType.equalsIgnoreCase("lumberjack")){
            amount = choppingwood(multiplier);
            itemtype = " wood";
        }else if(farmType.equalsIgnoreCase("mining")){
            amount = mine(multiplier);
            itemtype = " iron";
        }else if(farmType.equalsIgnoreCase("fishing")){
            amount = fishing(multiplier);
            itemtype = " fish";
        }
        addToCharacterStats(StatNames.valueOf(farmType.toUpperCase()),discordid,guildid,0.1/multiplier+0.01);
        return String.format("You farmed %s%s.",amount,itemtype);
    }

    public int choppingwood(double multiplier){
        return (int) (Math.random() * (10*multiplier) + 1);
    }

    public int mine(double multiplier){
        return (int) (Math.random() * (10*multiplier) + 1);
    }

    public int fishing(double multiplier){
        return (int) (Math.random() * (10*multiplier) + 1);
    }
}
