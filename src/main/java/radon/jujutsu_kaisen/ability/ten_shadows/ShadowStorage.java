package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.concurrent.atomic.AtomicBoolean;

public class ShadowStorage extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.4F);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                owner.level.addParticle(ParticleTypes.SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                        owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                        HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D);
                owner.level.addParticle(ParticleTypes.LARGE_SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                        owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                        HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D);
            }
        }
        
        if (owner.isShiftKeyDown()) {
            if (owner.getMainHandItem().isEmpty()) return;

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.addShadowInventory(owner.getMainHandItem());
                owner.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            });
        } else if (owner.level.isClientSide) {
            ClientWrapper.openShadowInventory();
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.isShiftKeyDown()) {
            if (owner.getMainHandItem().isEmpty()) result.set(true);
        } else {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.getShadowInventory().size() > 0));
        }
        return result.get() ? Status.FAILURE : super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
