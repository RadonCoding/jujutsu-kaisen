package radon.jujutsu_kaisen.client.render.item;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.YutaOkkotsuSword;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class YutaOkkotsuSwordRenderer  extends GeoItemRenderer<YutaOkkotsuSword> {
    public YutaOkkotsuSwordRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuta_okkotsu_sword")));
    }
}
