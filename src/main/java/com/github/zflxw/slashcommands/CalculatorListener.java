package com.github.zflxw.slashcommands;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;

public class CalculatorListener implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        if (interaction.getCommandName().equals("calculator")) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder().setColor(Color.decode("0x2ecc71")).setDescription("```                           0```"))
                    .addComponents(
                            ActionRow.of(
                                    Button.danger("btnOff", "Off"),
                                    Button.primary("btnSqrt", "√"),
                                    Button.primary("btnClear", "C"),
                                    Button.success("btnEquals", "=")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnSeven", "7"),
                                    Button.secondary("btnEight", "8"),
                                    Button.secondary("btnNine", "9"),
                                    Button.primary("btnDivide", "÷")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnFour", "4"),
                                    Button.secondary("btnFive", "5"),
                                    Button.secondary("btnSix", "6"),
                                    Button.primary("btnMultiply", "x")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnOne", "1"),
                                    Button.secondary("btnTwo", "2"),
                                    Button.secondary("btnThree", "3"),
                                    Button.primary("btnSubtract", "-")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnZero", "0"),
                                    Button.secondary("btnDot", "."),
                                    Button.secondary("btnToggle", "±"),
                                    Button.primary("btnAdd", "+")
                            )
                    )
                    .respond();
        }
    }
}
