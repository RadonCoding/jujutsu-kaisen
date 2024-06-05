package radon.jujutsu_kaisen.item.cursed_tool;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.HitenStaffRenderer;
import radon.jujutsu_kaisen.item.CursedToolItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class HitenStaffItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HitenStaffItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HitenStaffRenderer renderer;

            @Override
            public HitenStaffRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new HitenStaffRenderer();
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
