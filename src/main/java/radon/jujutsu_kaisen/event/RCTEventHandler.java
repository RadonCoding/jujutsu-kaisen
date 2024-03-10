package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class RCTEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (victim.getHealth() - event.getAmount() > 0.0F) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (data.isUnlocked(JJKAbilities.RCT1.get())) return;
            if (victim instanceof TamableAnimal tamable && tamable.isTame()) return;
            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) return;
            if (data.getType() != JujutsuType.SORCERER) return;
            if (SorcererUtil.getGrade(data.getExperience()).ordinal() < SorcererGrade.GRADE_1.ordinal()) return;

            int chance = ConfigHolder.SERVER.reverseCursedTechniqueChance.get();

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = victim.getItemInHand(hand);

                if (stack.is(Items.TOTEM_OF_UNDYING)) {
                    chance /= 2;
                    break;
                }
            }

            if (HelperMethods.RANDOM.nextInt(chance) != 0) return;

            victim.setHealth(victim.getMaxHealth() / 2);
            data.unlock(JJKAbilities.RCT1.get());

            if (victim instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
            }

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = victim.getItemInHand(hand);

                if (stack.is(Items.TOTEM_OF_UNDYING) && CommonHooks.onLivingUseTotem(victim, source, stack, hand)) {
                    stack.shrink(1);
                    break;
                }
            }
            event.setCanceled(true);
        }
    }
}
