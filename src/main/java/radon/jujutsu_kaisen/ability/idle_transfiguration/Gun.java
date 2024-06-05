package radon.jujutsu_kaisen.ability.idle_transfiguration;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Transformation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;
import radon.jujutsu_kaisen.entity.projectile.TransfiguredSoulProjectile;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Gun extends Transformation {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(5) == 0;
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
        return 0.2F;
    }

    @Override
    public boolean isReplacement() {
        return false;
    }

    @Override
    public Item getItem() {
        return JJKItems.GUN.get();
    }

    @Override
    public Part getBodyPart() {
        return Part.RIGHT_ARM;
    }

    @Override
    public void onRightClick(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IIdleTransfigurationData data = cap.getIdleTransfigurationData();

        if (data.getTransfiguredSouls() == 0) return;

        data.decreaseTransfiguredSouls();

        owner.swing(InteractionHand.MAIN_HAND);

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SHOOT.get(), SoundSource.MASTER, 1.0F, 1.0F);

        TransfiguredSoulProjectile soul = new TransfiguredSoulProjectile(owner);
        owner.level().addFreshEntity(soul);
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public float getSlimTranslation() {
        return 0.03125F;
    }
}