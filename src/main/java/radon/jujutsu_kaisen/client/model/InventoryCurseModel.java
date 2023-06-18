package radon.jujutsu_kaisen.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class InventoryCurseModel extends DefaultedItemGeoModel<InventoryCurseItem> {
    public InventoryCurseModel() {
        super(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/inventory_curse"));
    }
}