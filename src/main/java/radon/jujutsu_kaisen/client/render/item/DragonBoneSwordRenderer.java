package radon.jujutsu_kaisen.client.render.item;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneSwordItem;
import radon.jujutsu_kaisen.item.cursed_tool.PlayfulCloudItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DragonBoneSwordRenderer extends GeoItemRenderer<DragonBoneSwordItem> {
    public DragonBoneSwordRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "dragon_bone_sword")));
    }
}
