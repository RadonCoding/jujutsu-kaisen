package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TwentyFourFrameRule extends Ability implements Ability.IToggled, Ability.IAttack {
    private static final float DAMAGE = 15.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!DamageUtil.isMelee(source)) return false;

        for (ProjectionFrameEntity frame : owner.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(target.position(),
                8.0D, 8.0D, 8.0D))) {
            if (frame.getVictim() == target) return false;
        }

        owner.level().addFreshEntity(new ProjectionFrameEntity(owner, target, Ability.getPower(JJKAbilities.TWENTY_FOUR_FRAME_RULE.get(), owner)));

        if (target instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new ScreenFlashS2CPacket(), player);
        }
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.PROJECTION;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            for (ProjectionFrameEntity frame : victim.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(victim.position(),
                    8.0D, 8.0D, 8.0D))) {
                if (frame.getVictim() != victim) continue;

                Vec3 center = new Vec3(frame.getX(), frame.getY(), frame.getZ());
                ((ServerLevel) frame.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);

                frame.level().playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
                frame.level().playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                frame.discard();

                LivingEntity owner = frame.getOwner();

                if (owner == null) continue;

                if (victim.hurt(JJKDamageSources.indirectJujutsuAttack(frame, attacker, JJKAbilities.TWENTY_FOUR_FRAME_RULE.get()), DAMAGE * frame.getPower())) {
                    if (victim.isDeadOrDying()) {
                        event.setCanceled(true);
                    }
                }
                break;
            }
        }
    }
}