package radon.jujutsu_kaisen.client.render.item.armor;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InstantSpiritBodyOfDistortedKillingItem;
import radon.jujutsu_kaisen.item.armor.WingsItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WingsRenderer extends GeoArmorRenderer<WingsItem> {
    public WingsRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/wings")));
    }
}