package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.util.HelperMethods;


public class ShadowStorage extends Ability {
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
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.4F);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                owner.level().addParticle(ParticleTypes.SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                        owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                        HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D);
                owner.level().addParticle(ParticleTypes.LARGE_SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                        owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F),
                        HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D);
            }
        }

        if (owner.isShiftKeyDown()) {
            if (owner.getMainHandItem().isEmpty()) return;

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ITenShadowsData data = cap.getTenShadowsData();

            data.addShadowInventory(owner.getMainHandItem());
            owner.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        } else if (owner.level().isClientSide) {
            ClientWrapper.openShadowInventory();
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        ITenShadowsData data = cap.getTenShadowsData();

        if (owner.isShiftKeyDown()) {
            if (owner.getMainHandItem().isEmpty()) return Status.FAILURE;
        } else {
            if (data.getShadowInventory().isEmpty()) return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
