package radon.jujutsu_kaisen.client.gui.screen.radial;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.network.packet.c2s.UncopyC2SPacket;

public class CopiedDisplayItem extends CursedTechniqueDisplayItem {
    public CopiedDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, CursedTechnique technique) {
        super(minecraft, screen, select, technique);
    }

    @Override
    public void mouseClicked(int button) {
        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IMimicryData data = cap.getMimicryData();

        if (button != InputConstants.MOUSE_BUTTON_RIGHT) return;

        CursedTechnique copied = this.getTechnique();
        PacketDistributor.sendToServer(new UncopyC2SPacket(copied));
        data.uncopy(copied);
        this.screen.init();
    }

    @Override
    public boolean isActive() {
        if (this.minecraft.player == null) return false;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IMimicryData data = cap.getMimicryData();

        return data.getCurrentCopied() == this.getTechnique();
    }
}