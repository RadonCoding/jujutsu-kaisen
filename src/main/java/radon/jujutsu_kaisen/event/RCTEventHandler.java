package radon.jujutsu_kaisen.event;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class RCTEventHandler {
    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        private static boolean check(LivingEntity entity, int chance) {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return false;

            ISorcererData data = cap.getSorcererData();

            if (JJKAbilities.RCT1.get().isUnlocked(entity)) return false;
            if (entity instanceof TamableAnimal tamable && tamable.isTame()) return false;
            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) return false;
            if (data.getType() != JujutsuType.SORCERER) return false;
            if (SorcererUtil.getGrade(data.getExperience()).ordinal() < SorcererGrade.GRADE_1.ordinal()) return false;

            return HelperMethods.RANDOM.nextInt(chance) == 0;
        }

        @SubscribeEvent
        public static void onLivingUseTotem(LivingUseTotemEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (!check(victim, ConfigHolder.SERVER.reverseCursedTechniqueChance.get() / 2)) return;

            data.unlock(JJKAbilities.RCT1.get());

            if (victim instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
            }
            victim.setHealth(1.0F);
        }

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

            if (!check(victim, ConfigHolder.SERVER.reverseCursedTechniqueChance.get())) return;

            data.unlock(JJKAbilities.RCT1.get());

            if (victim instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
            }
            victim.setHealth(1.0F);
            event.setCanceled(true);
        }
    }
}
