package radon.jujutsu_kaisen.client.render.item;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.cursed_tool.HitenStaffItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HitenStaffRenderer extends GeoItemRenderer<HitenStaffItem> {
    public HitenStaffRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "hiten_staff")));
    }
}
