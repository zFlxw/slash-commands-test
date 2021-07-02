package com.github.zflxw.slashcommands;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.mariuszgromada.math.mxparser.Expression;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CalculatorListener implements SlashCommandCreateListener, MessageComponentCreateListener {
    // unused yet, will be implemented when multiple / user bound calculators are implemented
    private final Map<Long, Double> currentSum = new HashMap<>();

    String contentString = "";
    int defaultLength = 40;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        if (interaction.getCommandName().equals("calculator")) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder().setColor(Color.decode("0x2980b9")).setDescription("```" + formatString(String.valueOf(0)) + "```"))
                    .addComponents(
                            ActionRow.of(
                                    Button.danger("btnOff", "Off"),
                                    Button.primary("btnClear", "C"),
                                    Button.primary("btnRemLast", "←"),
                                    Button.primary("btnDivide", "÷"),
                                    Button.primary("btnSin", "SIN")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnSeven", "7"),
                                    Button.secondary("btnEight", "8"),
                                    Button.secondary("btnNine", "9"),
                                    Button.primary("btnMultiply", "x"),
                                    Button.primary("btnCos", "COS")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnFour", "4"),
                                    Button.secondary("btnFive", "5"),
                                    Button.secondary("btnSix", "6"),
                                    Button.primary("btnSubtract", "-"),
                                    Button.primary("btnTan", "TAN")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnOne", "1"),
                                    Button.secondary("btnTwo", "2"),
                                    Button.secondary("btnThree", "3"),
                                    Button.primary("btnAdd", "+"),
                                    Button.primary("btnPower", "x²")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnZero", "0"),
                                    Button.secondary("btnDot", "."),
                                    Button.secondary("btnToggle", "±"),
                                    Button.success("btnEquals", "="),
                                    Button.primary("btnSqrt", "²√")
                            )
                    )
                    .respond();

            contentString = "";
            currentSum.put(event.getSlashCommandInteraction().getUser().getId(), 0.0D);
        }
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();
        String display = "0";
        boolean clearContent = false;

        if (contentString.length() < defaultLength) {
            switch (interaction.getCustomId()) {
                case "btnOne" -> contentString = contentString.concat("1");
                case "btnTwo" -> contentString = contentString.concat("2");
                case "btnThree" -> contentString = contentString.concat("3");
                case "btnFour" -> contentString = contentString.concat("4");
                case "btnFive" -> contentString = contentString.concat("5");
                case "btnSix" -> contentString = contentString.concat("6");
                case "btnSeven" -> contentString = contentString.concat("7");
                case "btnEight" -> contentString = contentString.concat("8");
                case "btnNine" -> contentString = contentString.concat("9");
                case "btnZero" -> contentString = contentString.concat("0");
                case "btnDot" -> contentString = contentString.concat(".");
                case "btnToggle" -> {
                    if (contentString.startsWith("-")) {
                        contentString = contentString.substring(1);
                    } else {
                        contentString = "-".concat(contentString);
                    }
                }
            }
        }

        switch (interaction.getCustomId()) {
            case "btnOff" -> interaction.getMessage().ifPresent(Message::delete);
            case "btnClear" -> {
                display = "";
                clearContent = true;
            }
            case "btnRemLast" -> {
                if (contentString.length() > 0) {
                    contentString = contentString.substring(0, contentString.length() - 1);
                }
            }
            case "btnEquals" -> {
                contentString = calculate(contentString);
                clearContent = true;
            }
            case "btnAdd" -> contentString = contentString.concat(" + ");
            case "btnSubtract" -> contentString = contentString.concat(" - ");
            case "btnDivide" -> contentString = contentString.concat(" / ");
            case "btnMultiply" -> contentString = contentString.concat(" * ");
            case "btnSin" -> {
                try {
                    double result = Math.sin(Math.toRadians(Double.parseDouble(contentString)));
                    contentString = String.valueOf(result);
                    display = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnCos" -> {
                try {
                    double result = Math.cos(Math.toRadians(Double.parseDouble(contentString)));
                    contentString = String.valueOf(result);
                    display = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnTan" -> {
                try {
                    double result = Math.tan(Math.toRadians(Double.parseDouble(contentString)));
                    contentString = String.valueOf(result);
                    display = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnPower" -> {
                try {
                    double result = Math.pow(Double.parseDouble(contentString), 2);
                    contentString = String.valueOf(result);
                    display = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnSqrt" -> {
                try {
                    double result = Math.sqrt(Double.parseDouble(contentString));
                    contentString = String.valueOf(result);
                    display = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
        }

        if (interaction.getMessage().isEmpty()) {
            interaction.createImmediateResponder().setContent("Failed updating your content, because the message is null.");
            return;
        }

        if (display.equals("0")) {
            display = contentString;
        }

        EmbedBuilder embed = interaction.getMessage().get().getEmbeds().get(0).toBuilder();
        embed.setDescription("```" + formatString(display) + "```");

        if (clearContent) {
            contentString = "";
        }

        interaction.getMessage().get().edit(embed);
        interaction.createImmediateResponder().respond();
    }

    private String calculate(String input) {
        Expression expression = new Expression(input);

        return String.valueOf(expression.calculate());
    }

    private String formatString(String content) {
        return formatString(content, defaultLength);
    }

    private String formatString(String content, int width) {
        String[] lines = content.split("\n");
        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(new String(new char[width - content.length()]).replace('\0', ' ')).append(line).append("\n");
        }

        return builder.toString();
    }
}
