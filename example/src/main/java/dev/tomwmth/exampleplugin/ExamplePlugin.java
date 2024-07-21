package dev.tomwmth.exampleplugin;

import co.crystaldev.alpinecore.AlpinePlugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class ExamplePlugin extends AlpinePlugin {
    @Override
    public void onStart() {
        // Put startup logic here
    }

    @Override
    public void onStop() {
        // Put shutdown logic here
    }

    @Override
    public void setupVariables(@NotNull VariableConsumer variableConsumer) {
        variableConsumer.addVariable("prefix", "<dark_gray>[</dark_gray><gradient:#e81cff:#40c9ff>Example</gradient><dark_gray>]</dark_gray>");
    }
}
