package radon.jujutsu_kaisen.client.render.item.armor;


import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.ArmBladeItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ArmBladeRenderer extends GeoArmorRenderer<ArmBladeItem> {
    public ArmBladeRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "armor/arm_blade")));
    }
}