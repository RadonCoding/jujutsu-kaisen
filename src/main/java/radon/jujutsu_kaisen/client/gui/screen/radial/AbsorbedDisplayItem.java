package radon.jujutsu_kaisen.client.gui.screen.radial;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.client.Minecraft;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;

public class AbsorbedDisplayItem extends CursedTechniqueDisplayItem {
    public AbsorbedDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, CursedTechnique technique) {
        super(minecraft, screen, select, technique);
    }

    @Override
    public void mouseClicked(int button) {

    }

    @Override
    public boolean isActive() {
        if (this.minecraft.player == null) return false;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ICurseManipulationData data = cap.getCurseManipulationData();

        return data.getCurrentAbsorbed() == this.getTechnique();
    }
}