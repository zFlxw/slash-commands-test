package com.github.zflxw.slashcommands;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class Calculator {
    @Getter
    private final long userId;

    @Getter @Setter @NotNull
    private String content;

    public Calculator(long userId, @NotNull String content) {
        this.userId = userId;
        this.content = content;
    }
}
