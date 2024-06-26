package radon.jujutsu_kaisen.client.render.item;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.KatanaItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class KatanaRenderer extends GeoItemRenderer<KatanaItem> {
    public KatanaRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "katana")));
    }

    @Override
    public ResourceLocation getTextureLocation(KatanaItem animatable) {
        return new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/item/%s.png", BuiltInRegistries.ITEM.getKey(animatable).getPath()));
    }
}
