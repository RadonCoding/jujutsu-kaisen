package radon.jujutsu_kaisen.client.render.item;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.PlayfulCloudItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SplitSoulKatanaRenderer extends GeoItemRenderer<PlayfulCloudItem> {
    public SplitSoulKatanaRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "split_soul_katana")));
    }
}
