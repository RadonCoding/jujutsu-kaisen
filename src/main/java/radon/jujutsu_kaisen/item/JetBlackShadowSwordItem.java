package radon.jujutsu_kaisen.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.render.item.JetBlackShadowSwordRenderer;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class JetBlackShadowSwordItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public JetBlackShadowSwordItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_3;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private JetBlackShadowSwordRenderer renderer;

            @Override
            public @NotNull JetBlackShadowSwordRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new JetBlackShadowSwordRenderer();
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
