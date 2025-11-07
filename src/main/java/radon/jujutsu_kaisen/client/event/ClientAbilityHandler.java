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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.ITransformation;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.radial.AbilityScreen;
import radon.jujutsu_kaisen.client.gui.screen.radial.DomainScreen;
import radon.jujutsu_kaisen.client.gui.screen.radial.MeleeScreen;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.IRightClickInputListener;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;


public class ClientAbilityHandler {
    private static @Nullable Ability channeled;
    private static @Nullable KeyMapping current;
    private static boolean isChanneling;
    private static boolean isRightDown;

    private static void channel(@Nullable Ability ability, @Nullable KeyMapping key) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        channeled = ability;
        current = key;
        isChanneling = false;
    }

    public static Ability.Status trigger(Ability ability) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return Ability.Status.FAILURE;

        IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Ability.Status.FAILURE;

        IAbilityData data = cap.getAbilityData();

        if (ability.getActivationType(mc.player) == Ability.ActivationType.INSTANT) {
            ability.charge(mc.player);
            ability.addDuration(mc.player);

            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(mc.player, ability));
            ability.run(mc.player);
            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(mc.player, ability));
        } else if (ability.getActivationType(mc.player) == Ability.ActivationType.TOGGLED) {
            ability.addDuration(mc.player);

            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(mc.player, ability));
            data.toggle(ability);
            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(mc.player, ability));
        } else if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
            ability.addDuration(mc.player);

            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(mc.player, ability));
            data.channel(ability);
            NeoForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(mc.player, ability));
        }
        return Ability.Status.SUCCESS;
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ForgeEvents {
        // TODO: Move to client visuals
        @SubscribeEvent
        public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

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

            MobEffectInstance instance = mc.player.getEffect(JJKEffects.STUN);

            if ((instance != null && instance.getAmplifier() > 0) || mc.player.hasEffect(JJKEffects.UNLIMITED_VOID)) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }

        @SubscribeEvent
        public static void onClientTickPost(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            if (current != null && channeled != null) {
                boolean isHeld = current.isDown();

                if (isHeld) {
                    if (!isChanneling) {
                        PacketDistributor.sendToServer(new TriggerAbilityC2SPacket(channeled));
                    }
                    isChanneling = true;
                } else if (isChanneling) {
                    AbilityHandler.untrigger(mc.player, channeled);
                    PacketDistributor.sendToServer(new UntriggerAbilityC2SPacket(channeled));

                    channel(null, null);
                }
            }

            if (mc.player.getVehicle() instanceof IRightClickInputListener listener) {
                if (!isRightDown && mc.mouseHandler.isRightPressed()) {
                    listener.setDown(true);
                    PacketDistributor.sendToServer(new RightClickInputListenerC2SPacket(true));

                    isRightDown = true;
                } else if (isRightDown && !mc.mouseHandler.isRightPressed()) {
                    listener.setDown(false);
                    PacketDistributor.sendToServer(new RightClickInputListenerC2SPacket(false));

                    isRightDown = false;
                }
            }

            IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (JJKKeys.OPEN_JUJUTSU_MENU.consumeClick()) {
                mc.setScreen(new JujutsuScreen());
            }

            if (JJKKeys.SHOW_ABILITY_MENU.consumeClick()) {
                mc.setScreen(new AbilityScreen());
            }

            if (JJKKeys.SHOW_DOMAIN_MENU.consumeClick()) {
                mc.setScreen(new DomainScreen());
            }

            switch (ConfigHolder.CLIENT.meleeMenuType.get()) {
                case TOGGLE -> {
                    if (JJKKeys.ACTIVATE_MELEE_MENU.consumeClick()) {
                        mc.setScreen(new MeleeScreen());
                    }
                }
                case SCROLL -> {
                    if (JJKKeys.MELEE_MENU_UP.consumeClick()) {
                        AbilityOverlay.scroll(1);
                    }
                    if (JJKKeys.MELEE_MENU_DOWN.consumeClick()) {
                        AbilityOverlay.scroll(-1);
                    }
                }
            }

            if (JJKKeys.INCREASE_OUTPUT.consumeClick()) {
                PacketDistributor.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.INCREASE));
                data.increaseOutput();
            }

            if (JJKKeys.DECREASE_OUTPUT.consumeClick()) {
                PacketDistributor.sendToServer(new ChangeOutputC2SPacket(ChangeOutputC2SPacket.DECREASE));
                data.decreaseOutput();
            }

            if (JJKKeys.ACTIVATE_ABILITY.isDown()) {
                Ability ability = AbilityOverlay.getSelected();

                if (ability != null) {
                    if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                        if (channeled == null) {
                            channel(ability, JJKKeys.ACTIVATE_ABILITY);
                        }
                    } else if (JJKKeys.ACTIVATE_ABILITY.consumeClick()){
                        PacketDistributor.sendToServer(new TriggerAbilityC2SPacket(ability));
                    }
                }
            }

            if (JJKKeys.ACTIVATE_RCT_OR_HEAL.isDown()) {
                Ability rct = EntityUtil.getRCTTier(mc.player);
                Ability ability = data.getType() == JujutsuType.CURSE ? JJKAbilities.HEAL.get() : rct;

                if (ability != null) {
                    if (channeled == null) {
                        channel(ability, JJKKeys.ACTIVATE_RCT_OR_HEAL);
                    }
                }
            }

            if (JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD.isDown()) {
                Ability shield = JJKAbilities.CURSED_ENERGY_SHIELD.get();

                if (channeled == null) {
                    channel(shield, JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD);
                }
            }

            if (JJKKeys.DASH.consumeClick()) {
                PacketDistributor.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.DASH.get()));
            }
        }

        @SubscribeEvent
        public static void onMouseInputPre(InputEvent.MouseButton.Pre event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null) return;

            IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (event.getAction() == InputConstants.PRESS && event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT) {
                if (RotationUtil.getLookAtHit(mc.player, 64.0D, target -> target instanceof NyoiStaffEntity) instanceof EntityHitResult hit) {
                    PacketDistributor.sendToServer(new NyoiStaffSummonLightningC2SPacket(hit.getEntity().getUUID()));
                } else {
                    for (Ability ability : data.getToggled()) {
                        if (!(ability instanceof ITransformation transformation)) continue;

                        transformation.onRightClick(mc.player);
                        PacketDistributor.sendToServer(new TransformationRightClickC2SPacket(ability));
                    }
                }
            }
        }
    }
}