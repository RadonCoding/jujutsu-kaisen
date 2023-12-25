package radon.jujutsu_kaisen.client.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.screen.AbilityScreen;
import radon.jujutsu_kaisen.client.gui.screen.DomainScreen;
import radon.jujutsu_kaisen.client.gui.screen.ShadowInventoryScreen;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.JumpInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RightClickInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.awt.event.KeyEvent;


public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;
    private static boolean isRightDown;

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientAbilityHandlerForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (current != null) {
                boolean possiblyChanneling = channeled != null && channeled.getActivationType(mc.player) == Ability.ActivationType.CHANNELED;

                if (possiblyChanneling) {
                    boolean isHeld = current.isDown();

                    if (isHeld) {
                        if (!isChanneling) {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));
                            ClientAbilityHandler.trigger(channeled);
                        }
                        isChanneling = true;
                    } else if (isChanneling) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));
                        ClientAbilityHandler.trigger(channeled);

                        channeled = null;
                        current = null;
                        isChanneling = false;
                    }
                }
            }

            if (mc.player.getVehicle() instanceof IRightClickInputListener listener) {
                if (!isRightDown && mc.mouseHandler.isRightPressed()) {
                    PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(true));
                    listener.setDown(true);

                    isRightDown = true;
                } else if (isRightDown && !mc.mouseHandler.isRightPressed()) {
                    PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(false));
                    listener.setDown(false);

                    isRightDown = false;
                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (event.getKey() == KeyEvent.VK_SPACE) {
                if (mc.player.getVehicle() instanceof IJumpInputListener listener) {
                    PacketHandler.sendToServer(new JumpInputListenerC2SPacket(event.getAction() == InputConstants.PRESS));
                    listener.setJump(event.getAction() != InputConstants.RELEASE);
                } else if (mc.player.getFirstPassenger() instanceof IJumpInputListener listener) {
                    PacketHandler.sendToServer(new JumpInputListenerC2SPacket(event.getAction() == InputConstants.PRESS));
                    listener.setJump(event.getAction() != InputConstants.RELEASE);
                }
            }

            if (event.getAction() == InputConstants.PRESS) {
                if (JJKKeys.ACTIVATE_ABILITY.isDown()) {
                    Ability ability = AbilityOverlay.getSelected();

                    if (ability != null) {
                        if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                            channeled = ability;
                            current = JJKKeys.ACTIVATE_ABILITY;
                        } else {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                            ClientAbilityHandler.trigger(ability);
                        }
                    }
                }

                if (JJKKeys.ACTIVATE_RCT_OR_HEAL.isDown()) {
                    mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        channeled = cap.getType() == JujutsuType.CURSE ? JJKAbilities.HEAL.get() : HelperMethods.getRCTTier(mc.player);
                        current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                    });
                }

                if (JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD.isDown()) {
                    mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        channeled = JJKAbilities.CURSED_ENERGY_SHIELD.get();
                        current = JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD;
                    });
                }

                if (JJKKeys.DASH.isDown()) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.DASH.get())));
                    ClientAbilityHandler.trigger(JJKAbilities.DASH.get());
                }
            } else if (event.getAction() == InputConstants.RELEASE) {
                if ((event.getKey() == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue() && mc.screen instanceof AbilityScreen) ||
                        (event.getKey() == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue() && mc.screen instanceof DomainScreen) ||
                        (event.getKey() == JJKKeys.ACTIVATE_ABILITY.getKey().getValue() && mc.screen instanceof ShadowInventoryScreen)) {
                    mc.screen.onClose();
                }
            }
        }
    }

    public static boolean isSuccess(Ability ability, Ability.Status status) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        if (owner == null) return false;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        switch (status) {
            case ENERGY ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.energy", JujutsuKaisen.MOD_ID)), false);
            case COOLDOWN ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.cooldown", JujutsuKaisen.MOD_ID),
                            Math.max(1, cap.getRemainingCooldown(ability) / 20)), false);
            case BURNOUT ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.burnout", JujutsuKaisen.MOD_ID),
                            cap.getBurnout() / 20), false);
            case FAILURE ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.failure", JujutsuKaisen.MOD_ID)), false);
            case DOMAIN_AMPLIFICATION ->
                    mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.domain_amplification", JujutsuKaisen.MOD_ID)), false);
        }
        return status == Ability.Status.SUCCESS;
    }

    public static Ability.Status trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        if (owner == null) return Ability.Status.FAILURE;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            Ability.Status status;

            if (isSuccess(ability, (status = ability.isTriggerable(owner)))) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                ability.run(owner);
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            Ability.Status status;

            if (isSuccess(ability, (status = ability.isTriggerable(owner))) || cap.hasToggled(ability)) {
                if (cap.hasToggled(ability)) {
                    cap.toggle(ability);
                } else {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                    cap.toggle(ability);
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
                }
            }
            return status;
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            Ability.Status status;

            if (isSuccess(ability, (status = ability.isTriggerable(owner))) || cap.isChanneling(ability)) {
                if (cap.isChanneling(ability)) {
                    cap.channel(ability);
                } else {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
                    cap.channel(ability);
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
                }
            }
            return status;
        }
        return Ability.Status.SUCCESS;
    }
}
