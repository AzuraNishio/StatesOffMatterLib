package azura.test_mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class LiveTweaker {
    private static final KeyBinding INCREASE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tweaker.increase",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.tweaker"
    ));
    
    private static final KeyBinding DECREASE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tweaker.decrease",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "category.tweaker"
    ));
    
    private static final KeyBinding SWITCH_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tweaker.switch",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.tweaker"
    ));
    
    private static final List<Float> values = new ArrayList<>();
    private static int currentIndex = 0;
    private static final float STEP = 0.05f;
    static {
        values.add(0.5f);
    }
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            if (SWITCH_KEY.wasPressed()) {
                currentIndex = (currentIndex + 1) % values.size();
                sendMessage(client, "Now tweaking: Value[" + currentIndex + "]");
            }
            
            if (INCREASE_KEY.wasPressed()) {
                changeValue(STEP);
                sendMessage(client, getCurrentValueInfo());
            }
            
            if (DECREASE_KEY.wasPressed()) {
                changeValue(-STEP);
                sendMessage(client, getCurrentValueInfo());
            }
        });
    }
    
    public static float gv(int index) {
        // Auto-expand array if needed
        while (index >= values.size()) {
            values.add(0.5f);
        }
        return values.get(index);
    }

    public static boolean gp(int index) {
        while (index >= values.size()) {
            values.add(0.5f);
        }
        boolean state = values.get(index) != 0.5;
        values.set(index, 0.5F);
        return state;
    }
    
    private static void changeValue(float delta) {
        float newValue = Math.max(0f, Math.min(1f, values.get(currentIndex) + delta));
        values.set(currentIndex, newValue);
    }
    
    private static String getCurrentValueInfo() {
        return "Value[" + currentIndex + "]: " + String.format("%.3f", values.get(currentIndex));
    }
    
    private static void sendMessage(net.minecraft.client.MinecraftClient client, String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), false);
        }
    }
}