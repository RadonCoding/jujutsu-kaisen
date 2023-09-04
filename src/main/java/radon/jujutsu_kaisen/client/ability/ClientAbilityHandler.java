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
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.MeleeAbilityOverlay;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.JumpInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RightClickInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;

import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicReference;

public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;
    private static boolean isRightDown;

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
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
                    } else {
                        if (isChanneling) {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));
                            ClientAbilityHandler.trigger(channeled);

                            channeled = null;
                            current = null;
                            isChanneling = false;
                        }
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

            if (JJKKeys.ACTIVATE_ABILITY.isDown()) {
                Ability ability = MeleeAbilityOverlay.getSelected();

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
                    channeled = cap.getType() == JujutsuType.CURSE ? JJKAbilities.HEAL.get() : JJKAbilities.RCT.get();
                    current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                });
            }

            if (JJKKeys.ACTIVATE_WATER_WALKING.consumeClick()) {
                PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.WATER_WALKING.get())));
                ClientAbilityHandler.trigger(JJKAbilities.WATER_WALKING.get());
            }

            if (JJKKeys.DASH.consumeClick()) {
                PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.DASH.get())));
                ClientAbilityHandler.trigger(JJKAbilities.DASH.get());
            }

            if (event.getKey() == KeyEvent.VK_SPACE && mc.player.getVehicle() instanceof IJumpInputListener listener) {
                PacketHandler.sendToServer(new JumpInputListenerC2SPacket(event.getAction() == InputConstants.PRESS));
                listener.setJump(event.getAction() != InputConstants.RELEASE);
            }
        }
    }

    public static boolean isSuccess(Ability ability, Ability.Status status) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        assert owner != null;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            switch (status) {
                case ENERGY -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.energy", JujutsuKaisen.MOD_ID)), false);
                case COOLDOWN -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.cooldown", JujutsuKaisen.MOD_ID),
                        Math.max(1, cap.getRemainingCooldown(ability) / 20)), false);
                case BURNOUT -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.burnout", JujutsuKaisen.MOD_ID),
                        cap.getBurnout() / 20), false);
                case FAILURE -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.failure", JujutsuKaisen.MOD_ID)), false);
                case DOMAIN_AMPLIFICATION -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.domain_amplification", JujutsuKaisen.MOD_ID)), false);
                case SIMPLE_DOMAIN -> mc.gui.setOverlayMessage(Component.translatable(String.format("ability.%s.fail.simple_domain", JujutsuKaisen.MOD_ID)), false);
            }
        });
        return status == Ability.Status.SUCCESS;
    }

    public static Ability.Status trigger(Ability ability) {
        AtomicReference<Ability.Status> result = new AtomicReference<>(Ability.Status.SUCCESS);

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        assert owner != null;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (ability.getActivationType(mc.player) == Ability.ActivationType.INSTANT) {
                Ability.Status status;

                if (isSuccess(ability, (status = ability.checkTriggerable(owner)))) {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                    ability.run(owner);
                }
                result.set(status);
            } else if (ability.getActivationType(mc.player) == Ability.ActivationType.TOGGLED) {
                Ability.Status status;

                if (isSuccess(ability, (status = ability.checkToggleable(owner))) || cap.hasToggled(ability)) {
                    if (!cap.hasToggled(ability)) {
                        MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                    }
                    cap.toggle(owner, ability);
                }
                result.set(status);
            } else if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                Ability.Status status;

                if (isSuccess(ability, status = ability.checkChannelable(owner)) || cap.isChanneling(ability)) {
                    cap.channel(owner, ability);
                }
                result.set(status);
            }
        });
        return result.get();
    }
}
