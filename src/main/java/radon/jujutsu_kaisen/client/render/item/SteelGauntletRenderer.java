package radon.jujutsu_kaisen.client.render.item;


import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.SteelGauntletItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SteelGauntletRenderer extends GeoItemRenderer<SteelGauntletItem> {
    public SteelGauntletRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "steel_gauntlet")));
    }
}