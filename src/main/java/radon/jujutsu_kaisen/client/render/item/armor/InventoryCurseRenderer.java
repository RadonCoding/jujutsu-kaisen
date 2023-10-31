package radon.jujutsu_kaisen.client.render.item.armor;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class InventoryCurseRenderer extends GeoArmorRenderer<InventoryCurseItem> {
    public InventoryCurseRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/inventory_curse")));
    }
}