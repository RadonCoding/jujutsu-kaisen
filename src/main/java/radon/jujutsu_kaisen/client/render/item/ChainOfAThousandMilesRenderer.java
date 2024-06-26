package radon.jujutsu_kaisen.client.render.item;


import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.ChainOfAThousandMilesItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ChainOfAThousandMilesRenderer extends GeoItemRenderer<ChainOfAThousandMilesItem> {
    public ChainOfAThousandMilesRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "chain_of_a_thousand_miles")));
    }
}
