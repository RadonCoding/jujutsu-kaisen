package radon.jujutsu_kaisen.client.model.item.armor;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class InventoryCurseModel extends DefaultedItemGeoModel<InventoryCurseItem> {
    public InventoryCurseModel() {
        super(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/inventory_curse"));
    }
}