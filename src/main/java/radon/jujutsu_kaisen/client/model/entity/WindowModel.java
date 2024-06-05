package radon.jujutsu_kaisen.client.model.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.WindowEntity;

public class WindowModel extends SkinModel<WindowEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "window"), "main");
    public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "window"), "inner_armor");
    public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "window"), "outer_armor");

    public WindowModel(ModelPart pRoot) {
        super(pRoot);
    }
}
