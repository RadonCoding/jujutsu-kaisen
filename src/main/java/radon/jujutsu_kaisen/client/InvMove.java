package radon.jujutsu_kaisen.client;


import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import radon.jujutsu_kaisen.client.gui.screen.radial.RadialScreen;

import java.util.HashMap;
import java.util.Map;

public abstract class InvMove {
    private static final Map<ToggleKeyMapping, Boolean> wasToggleKeyDown = new HashMap<>();
    private static boolean wasSneaking = false;

    public static void onInputUpdate(Input input) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        if (input.getClass() != KeyboardInput.class || input != Minecraft.getInstance().player.input) {
            return;
        }

        if (Minecraft.getInstance().screen == null) {
            wasSneaking = input.shiftKeyDown;
        }

        boolean canMove = allowMovementInScreen(Minecraft.getInstance().screen);

        if (canMove) {
            for (KeyMapping k : KeyMapping.ALL.values()) {
                if (k.getKey().getType() == InputConstants.Type.KEYSYM && k.getKey().getValue() != InputConstants.UNKNOWN.getValue()) {
                    boolean raw = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), k.getKey().getValue());

                    if (k instanceof ToggleKeyMapping && ((ToggleKeyMapping) k).needsToggle.getAsBoolean()) {
                        if (wasToggleKeyDown.containsKey(k)) {
                            if (!wasToggleKeyDown.get(k) && raw) {
                                k.setDown(true);
                            }
                        }
                        wasToggleKeyDown.put((ToggleKeyMapping) k, raw);
                    } else {
                        k.setDown(raw);
                    }
                }
            }

            Minecraft.getInstance().options.keyDrop.setDown(false);

            if (!Minecraft.getInstance().options.toggleCrouch().get()) {
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isPassenger()) {
                    Minecraft.getInstance().options.keyShift.setDown(Minecraft.getInstance().options.keyShift.isDown());
                } else {
                    wasSneaking = Minecraft.getInstance().options.keyShift.isDown();
                    Minecraft.getInstance().options.keyShift.setDown(wasSneaking);
                }
            }
            manualTickMovement(input, Minecraft.getInstance().player.isMovingSlowly(), Minecraft.getInstance().player.isSpectator());
        } else if (Minecraft.getInstance().screen != null) {
            KeyMapping.releaseAll();

            if (!Minecraft.getInstance().options.toggleCrouch().get()) {
                if (Minecraft.getInstance().player == null || !Minecraft.getInstance().player.isPassenger()) {
                    Minecraft.getInstance().options.keyShift.setDown(wasSneaking);
                    input.shiftKeyDown = wasSneaking;
                }
            }
        }
    }

    public static boolean allowMovementInScreen(Screen screen) {
        return screen instanceof RadialScreen;
    }

    public static void manualTickMovement(Input input, boolean slow, boolean noDampening) {
        input.up = rawIsKeyDown(Minecraft.getInstance().options.keyUp);
        input.down = rawIsKeyDown(Minecraft.getInstance().options.keyDown);
        input.left = rawIsKeyDown(Minecraft.getInstance().options.keyLeft);
        input.right = rawIsKeyDown(Minecraft.getInstance().options.keyRight);
        input.forwardImpulse = input.up == input.down ? 0.0F : (float) (input.up ? 1 : -1);
        input.leftImpulse = input.left == input.right ? 0.0F : (float) (input.left ? 1 : -1);
        input.jumping = rawIsKeyDown(Minecraft.getInstance().options.keyJump);

        input.shiftKeyDown = rawIsKeyDown(Minecraft.getInstance().options.keyShift);

        if (!noDampening && (input.shiftKeyDown || slow)) {
            input.leftImpulse = (float) ((double) input.leftImpulse * 0.3D);
            input.forwardImpulse = (float) ((double) input.forwardImpulse * 0.3D);
        }
    }

    public static boolean rawIsKeyDown(KeyMapping key) {
        return key.isDown;
    }
}