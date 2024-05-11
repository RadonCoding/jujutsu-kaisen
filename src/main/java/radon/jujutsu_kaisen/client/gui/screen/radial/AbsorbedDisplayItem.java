package radon.jujutsu_kaisen.client.gui.screen.radial;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.network.packet.c2s.SetAbsorbedC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UncopyC2SPacket;

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