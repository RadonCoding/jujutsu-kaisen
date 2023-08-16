package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.TojiZeninEntity;

public class TojiZeninModel extends SkinModel<TojiZeninEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_zenin"), "main");

    public TojiZeninModel(ModelPart pRoot) {
        super(pRoot);
    }
}
