package radon.jujutsu_kaisen.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RikaModel extends DefaultedEntityGeoModel<RikaEntity> {
    public RikaModel() {
        super(new ResourceLocation(JujutsuKaisen.MOD_ID, "rika"), true);
    }
}