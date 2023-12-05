package radon.jujutsu_kaisen.ability.misc.lightning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.effect.EmittingLightningEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Discharge extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final float DAMAGE = 10.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return target != null && owner.distanceTo(target) <= 5.0D && owner.hasLineOfSight(target) && !JJKAbilities.hasToggled(owner, JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get()) &&
                owner.onGround() && cap.getType() == JujutsuType.CURSE || cap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE) ? owner.getHealth() / owner.getMaxHealth() < 0.9F : owner.getHealth() / owner.getMaxHealth() < 0.4F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 3.0F, 1.0F);

        for (int i = 0; i < 4; i++) {
            owner.level().addFreshEntity(new EmittingLightningEntity(owner, this.getPower(owner)));
        }

        for (Entity entity : owner.level().getEntities(owner, owner.getBoundingBox().inflate(16.0D))) {
            if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner))) {
                for (int i = 0; i < 4; i++) {
                    double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                    double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F);
                    double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                    level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                            x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                owner.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getNature() == CursedEnergyNature.LIGHTNING && JJKAbilities.hasToggled(owner, JJKAbilities.CURSED_ENERGY_FLOW.get()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 30.0F;
    }

    @Override
    public int getDuration() {
        return 5;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
