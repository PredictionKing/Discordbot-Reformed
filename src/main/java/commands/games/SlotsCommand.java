package commands.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static Main.Utils.CoinUtils.changeCoinsFromUser;
import static Main.Utils.CoinUtils.getCoinsFromUser;

public class SlotsCommand extends ListenerAdapter{
    Logger logger = LoggerFactory.getLogger(SlotsCommand.class);

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (!event.getName().equalsIgnoreCase("slots")) return;

        String discordId = event.getMember().getId();
        String guildid = event.getGuild().getId();

        int bet = event.getOption("bet-slots").getAsInt();
        double coinsFromUser = getCoinsFromUser(discordId, guildid);

        if (bet > coinsFromUser) {
            event.reply("You don't have enough coins.").setEphemeral(true).queue((msg) -> msg.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        event.deferReply().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83C\uDFB0 Slots \uD83C\uDFB0");
        eb.setDescription(String.format("You betted %s coins.", bet));
        eb.setFooter(String.format("Original bet %s coins.", bet));
        double coins = getCoinsFromUser(discordId, guildid);
        coins = round(coins, 2);
        eb.addField("User coins:", String.format("%s 💰", coins), false);


        event.getHook().sendMessageEmbeds(eb.build())
                .addActionRow(Button.danger("minus-25%", "-25%"), Button.danger("minus-10%", "-10%"), Button.primary(String.format("roll-%s", event.getMember().getId()), "Bet!"), Button.success("add-10%", "+10%"), Button.success("add-25%", "+25%"))
                //.addActionRow(Button.primary(String.format("roll-%s", event.getMember().getId()), "Bet!"))
                .queue();


    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        super.onButtonInteraction(event);

        String discordId = event.getMember().getId();
        String guildid = event.getGuild().getId();

        if (event.getButton().getId().equals(String.format("roll-%s", discordId))) {
            List<MessageEmbed> embeds = event.getMessage().getEmbeds();

            double bet = 0;
            double origBet = 0;

            for (MessageEmbed me : embeds) {
                if (me.getDescription() == null) return;
                bet = Double.parseDouble(me.getDescription().split(" ")[2]);
                bet = round(bet, 2);
                origBet = Double.parseDouble(me.getFooter().getText().split(" ")[2]);
                origBet = round(origBet, 2);
            }

            if (getCoinsFromUser(discordId, guildid) < bet) {
                event.reply("Not enough credits!").setEphemeral(true).queue();
            } else {
                if (bet < 0) event.reply("Can't roll negative!").setEphemeral(true).queue();
                logger.info(String.format("You bet %s", bet));
                event.editMessageEmbeds(createSlotEmbed(bet, true, origBet, discordId, guildid)).queue();
            }
        } else {

            List<MessageEmbed> embeds = event.getMessage().getEmbeds();
            double bet = 0;
            double origBet = 0;

            if (embeds.isEmpty() || embeds == null) return;

            for (MessageEmbed me : embeds) {
                if (me.getDescription() == null) return;
                bet = Double.parseDouble(me.getDescription().split(" ")[2]);
                bet = round(bet, 2);
                origBet = Double.parseDouble(me.getFooter().getText().split(" ")[2]);
                origBet = round(origBet, 2);
            }

            switch (event.getButton().getId()) {
                case "minus-25%":
                    event.editMessageEmbeds(createSlotEmbed(bet - origBet * 0.25, false, origBet, discordId,guildid)).queue();
                    break;
                case "minus-10%":
                    event.editMessageEmbeds(createSlotEmbed(bet - origBet * 0.1, false, origBet, discordId, guildid)).queue();
                    break;
                case "add-10%":
                    logger.info(String.format("You bet %s", bet));
                    event.editMessageEmbeds(createSlotEmbed(bet + origBet * 0.1, false, origBet, discordId, guildid)).queue();
                    break;
                case "add-25%":
                    logger.info(String.format("You bet %s", bet));
                    event.editMessageEmbeds(createSlotEmbed(bet + origBet * 0.25, false, origBet, discordId, guildid)).queue();
                    break;

            }
        }
    }

    public MessageEmbed createSlotEmbed(double bet, boolean withRoll, double origBet, String discordId, String guildid) {
        bet = round(bet, 2);
        EmbedBuilder eb = new EmbedBuilder();
        eb.getFields().clear();
        eb.setTitle("\uD83C\uDFB0 Slots \uD83C\uDFB0");
        eb.setDescription(String.format("You betted %s coins.", bet));
        eb.setFooter(String.format("Original bet %s coins.", origBet));

        if (withRoll) {
            BidiMap<Integer, String> slotSigns = new DualHashBidiMap<>();

            slotSigns.put(1, "\uD83C\uDF4B"); //🍋
            slotSigns.put(2, "\uD83C\uDF49"); //🍉
            slotSigns.put(3, "\uD83C\uDF1E"); //🌞
            slotSigns.put(4, "\uD83D\uDC0B"); //🐋
            slotSigns.put(5, "\uD83D\uDC8E"); //💎

            Set<Integer> keySet = slotSigns.keySet();
            List<Integer> keyList = new ArrayList<>(keySet);
            int size = keyList.size();

            String test = "";
            String[][] field = new String[3][3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int randIdx = (int) (Math.random() * (size) + 1);
                    System.out.println(randIdx);
                    if (j == 2) {
                        test += "[" + slotSigns.get(randIdx) + "]\n";
                        field[i][j] = slotSigns.get(randIdx);
                    } else {
                        test += "[" + slotSigns.get(randIdx) + "]";
                        field[i][j] = slotSigns.get(randIdx);
                    }
                }
            }

            if (field[1][0].equals(field[1][1]) && field[1][1].equals(field[1][2])) {
                int multi = slotSigns.getKey(field[1][0]);
                eb.addField("You win!", String.format("You won %s coins. Congrats!", bet * multi * 2), false);
                changeCoinsFromUser(discordId,guildid, bet * multi * 2);
                double coins = getCoinsFromUser(discordId, guildid);
                coins = round(coins, 2);
                eb.addField("User coins:", String.format("%s 💰", coins), false);
            } else {
                changeCoinsFromUser(discordId, guildid, -bet);
                double coins = getCoinsFromUser(discordId, guildid);
                coins = round(coins, 2);
                eb.addField("User coins:", String.format("%s 💰", coins), false);
            }

            eb.addField("Roll me!", test, false);
        }
        return eb.build();
    }
}
