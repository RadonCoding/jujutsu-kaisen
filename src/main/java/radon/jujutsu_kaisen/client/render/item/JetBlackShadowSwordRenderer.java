package radon.jujutsu_kaisen.client.render.item;


import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.JetBlackShadowSwordItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class JetBlackShadowSwordRenderer extends GeoItemRenderer<JetBlackShadowSwordItem> {
    public JetBlackShadowSwordRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "jet_black_shadow_sword")));
    }
}