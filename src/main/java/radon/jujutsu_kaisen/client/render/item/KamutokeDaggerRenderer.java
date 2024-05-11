package radon.jujutsu_kaisen.client.render.item;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.HitenStaffItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class KamutokeDaggerRenderer extends GeoItemRenderer<HitenStaffItem> {
    public KamutokeDaggerRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "kamutoke_dagger")));
    }
}
