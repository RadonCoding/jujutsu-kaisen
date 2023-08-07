package radon.jujutsu_kaisen.client.ability;

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
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;

public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;

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
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (JJKKeys.ACTIVATE_ABILITY.consumeClick()) {
                Ability ability = AbilityOverlay.getSelected();

                if (ability != null) {
                    if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                        mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            channeled = cap.isCurse() ? JJKAbilities.HEAL.get() : JJKAbilities.RCT.get();
                            current = JJKKeys.ACTIVATE_ABILITY;
                        });
                    } else {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                        ClientAbilityHandler.trigger(ability);
                    }
                }
            }

            if (JJKKeys.ACTIVATE_RCT_OR_HEAL.isDown()) {
                mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    channeled = cap.isCurse() ? JJKAbilities.HEAL.get() : JJKAbilities.RCT.get();
                    current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                });
            }

            if (JJKKeys.ACTIVATE_DOMAIN.consumeClick()) {
                mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    CursedTechnique technique = cap.getTechnique();

                    if (technique != null) {
                        Ability domain = technique.getDomain();

                        if (domain != null) {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(domain)));
                            ClientAbilityHandler.trigger(domain);
                        }
                    }
                });
            }

            if (JJKKeys.ABILITY_RIGHT.consumeClick()) {
                AbilityOverlay.scroll(1);
            } else if (JJKKeys.ABILITY_LEFT.consumeClick()) {
                AbilityOverlay.scroll(-1);
            }
        }
    }

    public static boolean isFailure(Ability ability, Ability.Status status) {
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
        return status != Ability.Status.SUCCESS;
    }

    public static void trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        assert owner != null;

        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID.get())) return;

        if (ability.getActivationType(mc.player) == Ability.ActivationType.INSTANT) {
            if (!isFailure(ability, ability.checkTriggerable(owner))) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                ability.run(owner);
            }
        } else if (ability.getActivationType(mc.player) == Ability.ActivationType.TOGGLED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.hasToggled(ability)) {
                    if (isFailure(ability, ability.checkToggleable(owner))) {
                        return;
                    }
                }
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                cap.toggle(owner, ability);
            });
        } else if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.isChanneling(ability)) {
                    if (isFailure(ability, ability.checkChannelable(owner))) {
                        return;
                    }
                }
                cap.channel(owner, ability);
            });
        }
    }
}
