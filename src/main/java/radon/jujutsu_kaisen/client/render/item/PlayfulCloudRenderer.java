package radon.jujutsu_kaisen.client.render.item;

import radon.jujutsu_kaisen.client.model.PlayfulCloudModel;
import radon.jujutsu_kaisen.item.PlayfulCloudItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PlayfulCloudRenderer extends GeoItemRenderer<PlayfulCloudItem> {
    public PlayfulCloudRenderer() {
        super(new PlayfulCloudModel());
    }
}
