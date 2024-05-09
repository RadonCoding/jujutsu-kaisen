package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IToggled;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.DamageUtil;

public class BlueFists extends Ability implements IToggled, IAttack {
    private static final float DAMAGE = 5.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.distanceTo(target) < 5.0D;
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
        return 1.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public Classification getClassification() {
        return Classification.BLUE;
    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!DamageUtil.isMelee(source)) return false;

        target.setDeltaMovement(owner.position().subtract(target.position()).normalize());
        target.hurtMarked = true;

        owner.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.MASTER, 1.0F, 1.0F);

        ((ServerLevel) owner.level()).getChunkSource().broadcastAndSend(owner, new ClientboundAnimatePacket(target, ClientboundAnimatePacket.CRITICAL_HIT));

        return target.hurt(JJKDamageSources.jujutsuAttack(owner, JJKAbilities.BLUE_FISTS.get()), DAMAGE * this.getOutput(owner));
    }
}
