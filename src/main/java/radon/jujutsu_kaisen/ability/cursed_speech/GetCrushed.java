package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.cursed_speech.util.CursedSpeechUtil;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class GetCrushed extends CursedSpeech {
    private static final float DAMAGE = 15.0F;
    private static final double CRUSH_POWER = 3.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;
        return CursedSpeechUtil.getTargets(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.level().isClientSide) return;

        CursedSpeechUtil.attack(owner, entity -> {
            if (!entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getOutput(owner))) return;

            Vec3 center = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);
            ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            owner.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                    4.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F);

            float radius = Math.min(10.0F, entity.getBbWidth() * entity.getBbHeight() * 2.0F);
            int minX = Mth.floor(entity.getX() - radius - 1.0F);
            int maxX = Mth.floor(entity.getX() + radius + 1.0F);
            int minY = Mth.floor(entity.getY() - radius - 1.0F);
            int maxY = Mth.floor(entity.getY() + radius + 1.0F);
            int minZ = Mth.floor(entity.getZ() - radius - 1.0F);
            int maxZ = Mth.floor(entity.getZ() + radius + 1.0F);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double distance = (x - entity.getX()) * (x - entity.getX()) +
                                (y - entity.getY()) * (y - entity.getY()) +
                                (z - entity.getZ()) * (z - entity.getZ());

                        if (distance <= radius * radius) {
                            BlockPos pos = new BlockPos(x, y, z);

                            if (HelperMethods.isDestroyable((ServerLevel) owner.level(), owner, owner, pos)) {
                                owner.level().destroyBlock(pos, false);
                            }
                        }
                    }
                }
            }
            entity.setDeltaMovement(0.0D, CRUSH_POWER * this.getOutput(owner) * -1.0D, 0.0D);
            entity.hurtMarked = true;

            if (entity instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.get_crushed", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        });
    }

    @Override
    public int getThroatDamage() {
        return 2 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 200.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
