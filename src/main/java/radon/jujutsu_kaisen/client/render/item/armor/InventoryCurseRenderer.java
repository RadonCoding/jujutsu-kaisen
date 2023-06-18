package radon.jujutsu_kaisen.client.render.item.armor;

import radon.jujutsu_kaisen.client.model.InventoryCurseModel;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class InventoryCurseRenderer  extends GeoArmorRenderer<InventoryCurseItem> {
    public InventoryCurseRenderer() {
        super(new InventoryCurseModel());
    }
}