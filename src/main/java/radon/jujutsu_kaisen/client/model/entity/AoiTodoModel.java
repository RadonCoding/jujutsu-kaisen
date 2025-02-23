package radon.jujutsu_kaisen.client.model.entity;


import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.AoiTodoEntity;

public class AoiTodoModel extends SkinModel<AoiTodoEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "aoi_todo"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "aoi_todo"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "aoi_todo"), "outer_armor");

    public AoiTodoModel(ModelPart pRoot) {
        super(pRoot);
    }
}
