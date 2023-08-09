package radon.jujutsu_kaisen.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import radon.jujutsu_kaisen.client.RecoilHandler;
import radon.jujutsu_kaisen.client.render.item.PistolRenderer;
import radon.jujutsu_kaisen.entity.projectile.BulletProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PistolItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PistolItem(Properties pProperties) {
        super(pProperties);
    }

    public static void shoot(ItemStack stack, LivingEntity owner) {
        if (stack.getItem() instanceof PistolItem) {
            BulletProjectile bullet = new BulletProjectile(owner);
            bullet.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, BulletProjectile.SPEED, 0.0F);
            owner.level.addFreshEntity(bullet);

            owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(),
                    JJKSounds.GUN.get(), SoundSource.MASTER, 2.0F, 1.0F / (HelperMethods.RANDOM.nextFloat() * 0.4F + 0.8F));

            if (owner.level.isClientSide) {
                RecoilHandler.fire();
            }
            stack.hurtAndBreak(1, owner, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PistolRenderer renderer;

            @Override
            public PistolRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new PistolRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
