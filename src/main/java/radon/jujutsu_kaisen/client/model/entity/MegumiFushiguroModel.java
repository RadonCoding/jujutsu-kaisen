package radon.jujutsu_kaisen.client.model.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.MegumiFushiguroEntity;

public class MegumiFushiguroModel extends SkinModel<MegumiFushiguroEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "megumi_fushiguro"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "megumi_fushiguro"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "megumi_fushiguro"), "outer_armor");

    public MegumiFushiguroModel(ModelPart pRoot) {
        super(pRoot);
    }
}
