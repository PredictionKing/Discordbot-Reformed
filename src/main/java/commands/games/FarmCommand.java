package commands.games;

import Main.Utils.Enums.StatNames;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Locale;

import static Main.Utils.CharacterStatsUtils.addToCharacterStats;
import static Main.Utils.CharacterStatsUtils.getCharacterStats;

public class FarmCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (!event.getName().equalsIgnoreCase("farm")) return;

        String farmType = event.getOption("farming-type").getAsString();
        String discordid = event.getUser().getId();
        String guildid = event.getGuild().getId();
        event.reply(farm(farmType, discordid, guildid)).queue();
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
