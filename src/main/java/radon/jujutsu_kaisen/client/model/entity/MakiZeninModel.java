package radon.jujutsu_kaisen.client.model.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.HajimeKashimoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.MakiZeninEntity;

public class MakiZeninModel extends SkinModel<MakiZeninEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "maki_zenin"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "maki_zenin"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "maki_zenin"), "outer_armor");

    public MakiZeninModel(ModelPart pRoot) {
        super(pRoot);
    }
}
