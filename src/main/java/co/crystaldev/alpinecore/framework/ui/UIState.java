package co.crystaldev.alpinecore.framework.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @since 0.4.0
 */
@RequiredArgsConstructor @Getter
final class UIState {

    private final Player player;

    private final Deque<UIContext> contextStack = new LinkedList<>();

    @Nullable
    public UIContext push(@NotNull UIContext context) {
        UIContext peek = this.peek();
        this.contextStack.push(context);
        return peek;
    }

    @Nullable
    public UIContext pop() {
        return this.contextStack.pop();
    }

    @Nullable
    public UIContext peek() {
        return this.contextStack.peek();
    }

    public boolean contains(@NotNull UIContext context) {
        return this.contextStack.contains(context);
    }

    public boolean isEmpty() {
        return this.contextStack.isEmpty();
    }
}
