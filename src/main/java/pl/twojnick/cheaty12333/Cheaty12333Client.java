package pl.twojnick.cheaty12333;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Cheaty12333Client implements ClientModInitializer {

    // === TWÓJ MODUŁ ESP ===
    public static final ESPModule ESP = new ESPModule();

    // Klawisz do włączania/wyłączania ESP (klawisz H)
    private static KeyBinding espKey;

    @Override
    public void onInitializeClient() {
        // Rejestracja klawisza H
        espKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cheaty12333.esp",      // nazwa w opcjach
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,            // klawisz H
                "category.cheaty12333"      // kategoria
        ));

        // Co tick sprawdzamy czy klawisz został naciśnięty
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (espKey.wasPressed()) {
                ESP.toggle();
            }
        });

        // === TO JEST TO, O CO PYTAŁEŚ ===
        // Rejestracja eventu do renderowania ESP
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ESP.render(context);
        });

        System.out.println("[Cheaty12333] Mod załadowany! Naciśnij H aby włączyć ESP.");
    }
}