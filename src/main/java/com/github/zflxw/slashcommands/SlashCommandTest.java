package com.github.zflxw.slashcommands;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.SlashCommand;

public class SlashCommandTest {
    private static SlashCommandTest instance;
    private final DiscordApi api;

    public static void main(String[] args) {
        new SlashCommandTest(args[0]);
    }

    public SlashCommandTest(String token) {
        instance = this;
        this.api = new DiscordApiBuilder()
                .setToken(token)
                .setAllIntents()
                .login().join();

        System.out.println("Servers: " + this.api.getServers().size());

        SlashCommand test = SlashCommand.with("test", "this is a test command").createForServer(this.api.getServers().stream().findFirst().get()).join();
        SlashCommand calculator = SlashCommand.with("calculator", "init a calculator").createForServer(this.api.getServers().stream().findFirst().get()).join();

        this.api.addSlashCommandCreateListener(new CalculatorListener());
        this.api.addMessageComponentCreateListener(new CalculatorListener());

    }

    public static SlashCommandTest getInstance() { return instance; }
    public DiscordApi getApi() { return api; }
}
