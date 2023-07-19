package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.YutaOkkotsuEntity;

public class YutaOkkotsuModel extends SkinModel<YutaOkkotsuEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuta_okkotsu"), "main");

    public YutaOkkotsuModel(ModelPart pRoot) {
        super(pRoot);
    }
}
