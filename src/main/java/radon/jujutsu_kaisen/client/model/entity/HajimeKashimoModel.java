package radon.jujutsu_kaisen.client.model.entity;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.HajimeKashimoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.YutaOkkotsuEntity;

public class HajimeKashimoModel extends SkinModel<HajimeKashimoEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "hajime_kashimo"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "hajime_kashimo"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "hajime_kashimo"), "outer_armor");

    public HajimeKashimoModel(ModelPart pRoot) {
        super(pRoot);
    }
}
