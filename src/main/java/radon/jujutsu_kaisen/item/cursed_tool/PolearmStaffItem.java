package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.PolearmStaffRenderer;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PolearmStaffItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PolearmStaffItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_4;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PolearmStaffRenderer renderer;

            @Override
            public PolearmStaffRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new PolearmStaffRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
