package radon.jujutsu_kaisen.client.model.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;

public class TojiFushiguroModel extends SkinModel<TojiFushiguroEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "toji_fushiguro"), "outer_armor");

    public TojiFushiguroModel(ModelPart pRoot) {
        super(pRoot);
    }
}
