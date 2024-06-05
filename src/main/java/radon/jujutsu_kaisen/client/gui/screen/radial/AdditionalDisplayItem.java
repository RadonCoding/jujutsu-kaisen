package radon.jujutsu_kaisen.client.gui.screen.radial;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveAdditionalC2SPacket;

public class AdditionalDisplayItem extends CursedTechniqueDisplayItem {
    public AdditionalDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, CursedTechnique technique) {
        super(minecraft, screen, select, technique);
    }

    @Override
    public void mouseClicked(int button) {
        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (button != InputConstants.MOUSE_BUTTON_RIGHT) return;

        CursedTechnique additional = this.getTechnique();
        PacketDistributor.sendToServer(new RemoveAdditionalC2SPacket(additional));
        data.removeAdditional(additional);
        this.screen.init();
    }

    @Override
    public boolean isActive() {
        if (this.minecraft.player == null) return false;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getCurrentAdditional() == this.getTechnique();
    }
}