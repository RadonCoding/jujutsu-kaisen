package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.HerobrineEntity;
import radon.jujutsu_kaisen.entity.sorcerer.MegumiFushiguroEntity;

public class HerobrineModel extends SkinModel<HerobrineEntity> {
    public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(JujutsuKaisen.MOD_ID, "herobrine"), "main");

    public HerobrineModel(ModelPart pRoot) {
        super(pRoot);
    }
}
