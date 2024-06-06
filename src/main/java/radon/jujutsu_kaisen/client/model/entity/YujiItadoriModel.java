package radon.jujutsu_kaisen.client.model.entity;


import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.YujiItadoriEntity;

public class YujiItadoriModel extends SkinModel<YujiItadoriEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuji_itadori"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuji_itadori"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "yuji_itadori"), "outer_armor");

    public YujiItadoriModel(ModelPart pRoot) {
        super(pRoot);
    }
}
