package radon.jujutsu_kaisen.client.render.item.armor;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.HorseLegsItem;
import radon.jujutsu_kaisen.item.armor.InstantSpiritBodyOfDistortedKillingItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HorseLegsRenderer extends GeoArmorRenderer<HorseLegsItem> {
    public HorseLegsRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/horse_legs")));
    }
}