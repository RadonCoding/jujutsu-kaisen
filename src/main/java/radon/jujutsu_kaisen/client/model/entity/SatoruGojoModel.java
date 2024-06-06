package radon.jujutsu_kaisen.client.model.entity;


import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.SatoruGojoEntity;

public class SatoruGojoModel extends SkinModel<SatoruGojoEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "satoru_gojo"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "satoru_gojo"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "satoru_gojo"), "outer_armor");

    public SatoruGojoModel(ModelPart pRoot) {
        super(pRoot);
    }
}
