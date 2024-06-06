package radon.jujutsu_kaisen.client.model.entity;


import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.NaoyaZeninEntity;

public class NaoyaZeninModel extends SkinModel<NaoyaZeninEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "naoya_zenin"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "naoya_zenin"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "naoya_zenin"), "outer_armor");

    public NaoyaZeninModel(ModelPart pRoot) {
        super(pRoot);
    }
}
