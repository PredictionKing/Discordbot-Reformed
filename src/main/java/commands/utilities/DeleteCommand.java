package commands.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeleteCommand extends ListenerAdapter {

int deleteCounterInt = 0;
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("delete")){

            OptionMapping deletecounter = event.getOption("lines");
            deleteCounterInt = deletecounter.getAsInt();
            List<Message> msg = event.getChannel().getHistory().retrievePast(deleteCounterInt).complete();

            String userName = event.getMember().getEffectiveName();
            String oldestMessage = msg.get(msg.size()-1).getContentRaw();
            String latestMessage = msg.get(0).getContentRaw();

            Collection<Button> buttons = new ArrayList();

            buttons.add(Button.success("delete", Emoji.fromUnicode("U+2705")));
            buttons.add(Button.danger("dontdelete", Emoji.fromUnicode("U+26D4")));




            EmbedBuilder requestBuilder = new EmbedBuilder();
            requestBuilder.setTitle("Are you sure❓ ️")
                    .setDescription("messages that got deleted, cant be restored again ⚠️ Choose wiseley bruh")
                    .addField("oldest message ", oldestMessage, false)
                    .addField("latest message", latestMessage, false)
                    .addField("Some messages can't be shown here", "so don't wonder if its blank", false)
                    .setFooter(String.format("used by %s",userName))
                    .setColor(Color.RED);


            MessageEmbed msgEmbed2 = requestBuilder.build();

            event.replyEmbeds(msgEmbed2)
                    .addActionRow(buttons)
                    .queue(message-> message.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));


        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        String userName = event.getMember().getEffectiveName();

        if (event.getButton().getId().equals("delete")) {

            List<Message> msg = event.getChannel().getHistory().retrievePast(deleteCounterInt+1).complete();
            String latestMessage = msg.get(0+1).getContentRaw();


            event.getChannel().purgeMessages(msg);

            EmbedBuilder deletedEmbed = new EmbedBuilder();
            deletedEmbed.setTitle(String.format("I got you... cleaning up \uD83E\uDDF9", deleteCounterInt))
                    .setDescription((String.format("%s messages got deleted ♻️", deleteCounterInt)))
                    .addField(String.format("Latest message was %s",latestMessage),"",true)
                    .setFooter(String.format("used by %s",userName))
                    .setColor(Color.GREEN);


            MessageEmbed msgEmbed = deletedEmbed.build();

            event.replyEmbeds(msgEmbed).queue((message) -> {
                message.deleteOriginal().queueAfter(15, TimeUnit.SECONDS);
            });
        }
        else if (event.getButton().getId().equals("dontdelete")) {
            EmbedBuilder deletedEmbed = new EmbedBuilder();
            deletedEmbed.setTitle(String.format("No Messages got deleted! ⛔️"))
                    .setDescription("Dont worry, you did nothing.")
                    .setFooter(String.format("used by %s",userName))
                    .setColor(Color.RED);


            MessageEmbed msgEmbed = deletedEmbed.build();

            event.replyEmbeds(msgEmbed).queue((message) -> {
                message.deleteOriginal().queueAfter(15, TimeUnit.SECONDS);
            });


        }
    }




}
