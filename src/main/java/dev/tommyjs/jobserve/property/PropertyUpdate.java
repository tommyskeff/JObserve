package dev.tommyjs.jobserve.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PropertyUpdate(@NotNull String key, @Nullable Object prev, @Nullable Object curr) {
}
