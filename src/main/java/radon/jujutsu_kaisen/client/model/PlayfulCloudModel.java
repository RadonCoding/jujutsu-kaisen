package radon.jujutsu_kaisen.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.PlayfulCloudItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class PlayfulCloudModel extends DefaultedItemGeoModel<PlayfulCloudItem> {
    public PlayfulCloudModel() {
        super(new ResourceLocation(JujutsuKaisen.MOD_ID, "playful_cloud"));
    }
}
