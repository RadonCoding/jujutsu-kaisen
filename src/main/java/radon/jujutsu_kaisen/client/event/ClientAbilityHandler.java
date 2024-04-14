package radon.jujutsu_kaisen.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.screen.*;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;


public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;
    private static boolean isRightDown;

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
            Minecraft mc = Minecraft.getInstance();

            assert mc.player != null;

            LivingEntity target = event.getEntity();

            ClientVisualHandler.ClientData client = ClientVisualHandler.get(target);

            if (client == null) return;

            if (!client.traits.contains(Trait.HEAVENLY_RESTRICTION_BODY)) return;

            if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity viewer)) return;

            IJujutsuCapability cap = viewer.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) return;

            if (target != viewer) {
                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(viewer);
                Vec3 start = viewer.getEyePosition();
                Vec3 result = target.getEyePosition().subtract(start);

                double angle = Math.acos(look.normalize().dot(result.normalize()));

                double threshold = 0.5D;

                if (target.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItemTags.CURSED_TOOL) ||
                        target.getItemInHand(InteractionHand.OFF_HAND).is(JJKItemTags.CURSED_TOOL)) {
                    threshold = 1.0D;
                }

                if (angle > threshold) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onInteractionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            MobEffectInstance instance = mc.player.getEffect(JJKEffects.STUN.get());

            if ((instance != null && instance.getAmplifier() > 0) || mc.player.hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (current != null) {
                boolean possiblyChanneling = channeled != null;

                if (possiblyChanneling) {
                    boolean isHeld = current.isDown();

                    if (isHeld) {
                        if (!isChanneling) {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));
                        }
                        isChanneling = true;
                    } else if (isChanneling) {
                        AbilityHandler.untrigger(mc.player, channeled);
                        PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));

                        channeled = null;
                        current = null;
                        isChanneling = false;
                    }
                }
            }

            if (mc.player.getVehicle() instanceof IRightClickInputListener listener) {
                if (!isRightDown && mc.mouseHandler.isRightPressed()) {
                    listener.setDown(true);
                    PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(true));

                    isRightDown = true;
                } else if (isRightDown && !mc.mouseHandler.isRightPressed()) {
                    listener.setDown(false);
                    PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(false));

                    isRightDown = false;
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerMouseClick(InputEvent.MouseButton.Pre event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (event.getAction() == InputConstants.PRESS && event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT) {
                if (RotationUtil.getLookAtHit(mc.player, 64.0D, target -> target instanceof NyoiStaffEntity) instanceof EntityHitResult hit) {
                    PacketHandler.sendToServer(new NyoiStaffSummonLightningC2SPacket(hit.getEntity().getUUID()));
                } else {
                    for (Ability ability : data.getToggled()) {
                        if (!(ability instanceof ITransformation transformation)) continue;
                        transformation.onRightClick(mc.player);
                        PacketHandler.sendToServer(new TransformationRightClickC2SPacket(JJKAbilities.getKey(ability)));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (event.getAction() == InputConstants.PRESS) {
                if (JJKKeys.OPEN_JUJUTSU_MENU.isDown()) {
                    mc.setScreen(new JujutsuScreen());
                }

                if (JJKKeys.SHOW_ABILITY_MENU.isDown()) {
                    mc.setScreen(new AbilityScreen());
                }

                if (JJKKeys.SHOW_DOMAIN_MENU.isDown()) {
                    mc.setScreen(new DomainScreen());
                }

                switch (ConfigHolder.CLIENT.meleeMenuType.get()) {
                    case TOGGLE -> {
                        if (JJKKeys.ACTIVATE_MELEE_MENU.isDown()) {
                            mc.setScreen(new MeleeScreen());
                        }
                    }
                    case SCROLL -> {
                        if (JJKKeys.MELEE_MENU_UP.isDown()) {
                            AbilityOverlay.scroll(1);
                        }
                        if (JJKKeys.MELEE_MENU_DOWN.isDown()) {
                            AbilityOverlay.scroll(-1);
                        }
                    }
                }

                if (JJKKeys.INCREASE_OUTPUT.consumeClick()) {
                    PacketHandler.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.INCREASE));
                    data.increaseOutput();
                }

                if (JJKKeys.DECREASE_OUTPUT.consumeClick()) {
                    PacketHandler.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.DECREASE));
                    data.decreaseOutput();
                }

                if (JJKKeys.ACTIVATE_ABILITY.isDown()) {
                    Ability ability = AbilityOverlay.getSelected();

                    if (ability != null) {
                        if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                            channeled = ability;
                            current = JJKKeys.ACTIVATE_ABILITY;
                        } else {
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                        }
                    }
                }

                if (JJKKeys.ACTIVATE_RCT_OR_HEAL.isDown()) {
                    Ability rct = EntityUtil.getRCTTier(mc.player);

                    if (data.getType() == JujutsuType.CURSE) {
                        channeled = JJKAbilities.HEAL.get();
                        current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                    } else if (rct != null) {
                        channeled = rct;
                        current = JJKKeys.ACTIVATE_RCT_OR_HEAL;
                    }
                }

                if (JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD.isDown()) {
                    channeled = JJKAbilities.CURSED_ENERGY_SHIELD.get();
                    current = JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD;
                }

                if (JJKKeys.DASH.isDown()) {
                    PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(JJKAbilities.DASH.get())));
                }
            } else if (event.getAction() == InputConstants.RELEASE) {
                if (current != null) {
                    boolean possiblyChanneling = channeled != null;

                    if (possiblyChanneling) {
                        if (event.getKey() == current.getKey().getValue()) {
                            AbilityHandler.untrigger(mc.player, channeled);
                            PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(channeled)));

                            channeled = null;
                            current = null;
                            isChanneling = false;
                        }
                    }
                }
                if ((event.getKey() == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue() && mc.screen instanceof AbilityScreen) ||
                        (event.getKey() == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue() && mc.screen instanceof DomainScreen) ||
                        (event.getKey() == JJKKeys.ACTIVATE_ABILITY.getKey().getValue() && mc.screen instanceof ShadowInventoryScreen)) {
                    mc.screen.onClose();
                }
            }
        }
    }

    public static Ability.Status trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer owner = mc.player;

        if (owner == null) return Ability.Status.FAILURE;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Ability.Status.FAILURE;

        IAbilityData data = cap.getAbilityData();

        if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            ability.charge(owner);
            ability.addDuration(owner);

            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            ability.run(owner);
            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
        } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            ability.addDuration(owner);

            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            data.toggle(ability);
            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
        } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            ability.addDuration(owner);

            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            data.channel(ability);
            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
        }
        return Ability.Status.SUCCESS;
    }
}
