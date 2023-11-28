package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CurseAbsorption extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static void makePoofParticles(Entity entity) {
        for (int i = 0; i < 20; ++i) {
            double d0 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d1 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d2 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D),
                    0, d0, d1, d2, 1.0D);
        }
    }

    public static boolean canAbsorb(LivingEntity owner, Entity entity) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return entity instanceof CursedSpirit curse && !curse.isTame() &&
                (HelperMethods.getGrade(cap.getExperience()).ordinal() - curse.getGrade().ordinal() >= 2 || curse.isDeadOrDying());
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            boolean melee = (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource src && src.getAbility() != null && src.getAbility().isMelee())
                    || !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SOUL));

            if (!melee) return;

            LivingEntity victim = event.getEntity();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (!canAbsorb(attacker, victim)) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!attackerCap.hasToggled(JJKAbilities.CURSE_ABSORPTION.get())) return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (victimCap.getType() != JujutsuType.CURSE) return;

            attacker.swing(InteractionHand.MAIN_HAND, true);

            Registry<EntityType<?>> registry = attacker.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            ResourceLocation key = registry.getKey(victim.getType());

            if (key == null) return;

            ItemStack stack = new ItemStack(JJKItems.CURSED_SPIRIT_ORB.get());
            CursedSpiritOrbItem.setKey(stack, key);

            if (attacker instanceof Player player) {
                player.addItem(stack);
            } else {
                attacker.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }
            makePoofParticles(victim);
            victim.discard();

            if (attacker instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
            }
        }
    }
}
