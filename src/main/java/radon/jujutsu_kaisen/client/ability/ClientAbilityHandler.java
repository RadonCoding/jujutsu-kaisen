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
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;

import java.util.concurrent.atomic.AtomicReference;

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

            if (JJKKeys.ACTIVATE_DOMAIN_OR_SIMPLE_DOMAIN.consumeClick()) {
                mc.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    boolean triggered = false;

                    if (cap.hasTrait(Trait.DOMAIN_EXPANSION)) {
                        CursedTechnique technique = cap.getTechnique();

                        if (technique != null) {
                            Ability domain = technique.getDomain();

                            if (domain != null) {
                                PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(domain)));
                                triggered = ClientAbilityHandler.trigger(domain) == Ability.Status.SUCCESS;
                            }
                        }
                    }
                    if (!triggered && cap.hasTrait(Trait.SIMPLE_DOMAIN)) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.SIMPLE_DOMAIN.get())));
                        ClientAbilityHandler.trigger(JJKAbilities.SIMPLE_DOMAIN.get());
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

        if (ability.getActivationType(mc.player) == Ability.ActivationType.INSTANT) {
            Ability.Status status;

            if (isSuccess(ability, (status = ability.checkTriggerable(owner)))) {
                MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                ability.run(owner);
            }
            result.set(status);
        } else if (ability.getActivationType(mc.player) == Ability.ActivationType.TOGGLED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Ability.Status status;

                if (isSuccess(ability, (status = ability.checkToggleable(owner))) || cap.hasToggled(ability)) {
                    MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent(owner, ability));
                    cap.toggle(owner, ability);
                }
                result.set(status);
            });
        } else if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Ability.Status status;

                if (isSuccess(ability, status = ability.checkChannelable(owner))) {
                    cap.channel(owner, ability);
                }
                result.set(status);
            });
        }
        return result.get();
    }
}
