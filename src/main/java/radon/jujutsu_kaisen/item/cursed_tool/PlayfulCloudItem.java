package radon.jujutsu_kaisen.item.cursed_tool;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.PlayfulCloudRenderer;
import radon.jujutsu_kaisen.item.CursedToolItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PlayfulCloudItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlayfulCloudItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public float doHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim, float amount) {
        victim.knockback(5.0F,
                Mth.sin(attacker.getYRot() * (float) (Math.PI / 180.0D)),
                -Mth.cos(attacker.getYRot() * (float) (Math.PI / 180.0D)));
        return (float) Math.pow(amount, 1.25D);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PlayfulCloudRenderer renderer;

            @Override
            public @NotNull PlayfulCloudRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new PlayfulCloudRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
