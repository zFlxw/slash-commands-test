package com.github.zflxw.slashcommands;

import org.apache.commons.lang3.StringUtils;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CalculatorListener implements SlashCommandCreateListener, MessageComponentCreateListener {
    private final Map<Long, Double> currentSum = new HashMap<>();

    String contentString = "";
    int defaultLength = 37;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();

        if (interaction.getCommandName().equals("calculator")) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder().setColor(Color.decode("0x2980b9")).setDescription("```" + formatString("0") + "```"))
                    .addComponents(
                            ActionRow.of(
                                    Button.danger("btnOff", "Off"),
                                    Button.primary("btnClear", "C"),
                                    Button.primary("btnRemLast", "←"),
                                    Button.success("btnEquals", "="),
                                    Button.primary("btnSin", "SIN")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnSeven", "7"),
                                    Button.secondary("btnEight", "8"),
                                    Button.secondary("btnNine", "9"),
                                    Button.primary("btnDivide", "÷"),
                                    Button.primary("btnCos", "COS")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnFour", "4"),
                                    Button.secondary("btnFive", "5"),
                                    Button.secondary("btnSix", "6"),
                                    Button.primary("btnMultiply", "x"),
                                    Button.primary("btnTan", "TAN")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnOne", "1"),
                                    Button.secondary("btnTwo", "2"),
                                    Button.secondary("btnThree", "3"),
                                    Button.primary("btnSubtract", "-"),
                                    Button.primary("btnPower", "x²")
                            ),
                            ActionRow.of(
                                    Button.secondary("btnZero", "0"),
                                    Button.secondary("btnDot", "."),
                                    Button.secondary("btnToggle", "±"),
                                    Button.primary("btnAdd", "+"),
                                    Button.primary("btnSqrt", "²√")
                            )
                    )
                    .respond();

            contentString = "0";
            currentSum.put(event.getSlashCommandInteraction().getUser().getId(), 0.0D);
        }
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();
        String display = "";

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
                    if (contentString.startsWith("-"))
                        contentString = contentString.substring(1);
                    else
                        contentString = "-".concat(contentString);
                }
            }
        }

        switch (interaction.getCustomId()) {
            case "btnOff" -> interaction.getMessage().ifPresent(Message::delete);
            case "btnClear" -> {
                contentString = "";
                display = "";
            }
            case "btnRemLast" -> contentString = contentString.substring(0, contentString.length() - 1);
            case "btnEquals" -> contentString = calculate(contentString);
            case "btnSin" -> {
                try {
                    double result = Math.sin(Math.toRadians(Double.parseDouble(contentString)));
                    contentString = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnCos" -> {
                try {
                    double result = Math.cos(Math.toRadians(Double.parseDouble(contentString)));
                    contentString = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnTan" -> {
                try {
                    double result = Math.tan(Math.toRadians(Double.parseDouble(contentString)));
                    contentString = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnPower" -> {
                try {
                    double result = Math.pow(Double.parseDouble(contentString), 2);
                    contentString = result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
            case "btnSqrt" -> {
                try {
                    double result = Math.sqrt(Double.parseDouble(contentString));
                    contentString = contentString + "\n" + result + " =";
                } catch (NumberFormatException exception) {
                    contentString = "";
                    display = "Syntax Error";
                }
            }
        }

        EmbedBuilder embed = interaction.getMessage().get().getEmbeds().get(0).toBuilder();
        embed.setDescription("```" + ((display.equals("")) ? formatString(contentString) : formatString(display)) + "```");

        interaction.getMessage().get().edit(embed);
        interaction.createImmediateResponder().respond();
    }

    private String calculate(String expression) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");

        try {
            return String.valueOf(engine.eval(expression.replaceAll("\\s+", "")));
        } catch (ScriptException e) {
            return "Error";
        }
    }

    private String formatString(String content) {
        return formatString(content, 37);
    }

    private String formatString(String content, int width) {
        int matches = StringUtils.countMatches(content, "\n");
        return new String(new char[(width * Math.max(1, matches)) - content.length()]).replace('\0', ' ') + content;
    }
}
