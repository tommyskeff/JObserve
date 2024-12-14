package dev.tommyjs.jobserve.property;

import org.jetbrains.annotations.NotNull;

public record PropertyEntry(@NotNull String key, @NotNull Object data) {
}