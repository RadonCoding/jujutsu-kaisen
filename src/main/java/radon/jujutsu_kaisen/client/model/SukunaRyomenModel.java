package radon.jujutsu_kaisen.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.SukunaRyomenEntity;

public class SukunaRyomenModel extends SkinModel<SukunaRyomenEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "sukuna_ryomen"), "main");

    public SukunaRyomenModel(ModelPart pRoot) {
        super(pRoot);
    }
}
