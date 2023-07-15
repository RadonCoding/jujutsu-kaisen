package radon.jujutsu_kaisen.client.model;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.RugbyFieldCurseEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RugbyFieldCurseModel extends DefaultedEntityGeoModel<RugbyFieldCurseEntity> {
    public RugbyFieldCurseModel() {
        super(new ResourceLocation(JujutsuKaisen.MOD_ID, "rugby_field_curse"), true);
    }
}