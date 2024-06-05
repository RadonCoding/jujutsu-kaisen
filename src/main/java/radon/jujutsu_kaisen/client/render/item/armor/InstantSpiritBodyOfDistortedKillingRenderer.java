package radon.jujutsu_kaisen.client.render.item.armor;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InstantSpiritBodyOfDistortedKillingItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class InstantSpiritBodyOfDistortedKillingRenderer extends GeoArmorRenderer<InstantSpiritBodyOfDistortedKillingItem> {
    public InstantSpiritBodyOfDistortedKillingRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/instant_spirit_body_of_distorted_killing")));
    }
}