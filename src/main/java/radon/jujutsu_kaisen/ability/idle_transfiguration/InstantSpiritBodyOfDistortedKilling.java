package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class InstantSpiritBodyOfDistortedKilling extends Ability implements Ability.IToggled, ITransformation {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        int count = (int) (owner.getBbWidth() * owner.getBbHeight()) * 8;

        for (int i = 0; i < count; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2) - owner.getLookAngle().scale(0.35D).x();
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2) - owner.getLookAngle().scale(0.35D).z();
            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
        }
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
    public Item getItem() {
        return JJKItems.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get();
    }

    @Override
    public boolean isFullBody() {
        return true;
    }
}