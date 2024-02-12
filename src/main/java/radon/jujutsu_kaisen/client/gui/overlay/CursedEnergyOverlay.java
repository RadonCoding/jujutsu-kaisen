package radon.jujutsu_kaisen.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.client.particle.ParticleColors;

import java.util.ArrayList;
import java.util.List;

public class CursedEnergyOverlay {
    public static ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/overlay/energy_bar.png");

    private static final float SCALE = 0.6F;

    public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();

        if (mc.player == null) return;

        IJujutsuCapability jujutsuCap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return;

        ISorcererData data = jujutsuCap.getSorcererData();

        graphics.pose().pushPose();
        graphics.pose().scale(SCALE, SCALE, SCALE);

        List<Component> above = new ArrayList<>();
        above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.output", JujutsuKaisen.MOD_ID), Math.round(data.getOutput() * 100)));
        above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.experience", JujutsuKaisen.MOD_ID), data.getExperience()));

        if (JJKAbilities.hasActiveTechnique(mc.player, JJKCursedTechniques.IDLE_TRANSFIGURATION.get())) {
            above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.transfigured_souls", JujutsuKaisen.MOD_ID), data.getTransfiguredSouls()));
        }

        int aboveY = 26;

        for (Component line : above) {
            graphics.drawString(gui.getFont(), line, Math.round(20 * (1.0F / SCALE)), Math.round(aboveY * (1.0F / SCALE)), 16777215);
            aboveY += mc.font.lineHeight - 1;
        }
        graphics.pose().popPose();

        if (data.getEnergy() > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Vector3f color = data.isInZone() ? ParticleColors.BLACK_FLASH : Vec3.fromRGB24(data.getCursedEnergyColor()).toVector3f();
            RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);

            graphics.blit(TEXTURE, 20, aboveY, 0, 0, 93, 10, 93, 18);

            float energyWidth = (data.getEnergy() / data.getMaxEnergy()) * 94.0F;
            graphics.blit(TEXTURE, 20, aboveY + 1, 0, 10, (int) energyWidth, 8, 93, 18);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        graphics.pose().pushPose();
        graphics.pose().scale(SCALE, SCALE, SCALE);

        if (data.getEnergy() > 0.0F) {
            graphics.drawString(gui.getFont(), String.format("%.1f / %.1f", data.getEnergy(), data.getMaxEnergy()),
                    Math.round(23 * (1.0F / SCALE)), Math.round((aboveY + 3.0F) * (1.0F / SCALE)), 16777215);
            aboveY += 4;
        }

        List<Component> below = new ArrayList<>();

        for (Ability ability : data.getToggled()) {
            if (!(ability instanceof Ability.IAttack)) continue;

            int cooldown = data.getRemainingCooldown(ability);

            if (cooldown > 0) {
                below.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.cooldown", JujutsuKaisen.MOD_ID), ability.getName(), Math.round((float) cooldown / 20)));
            }
        }

        Ability channeled = data.getChanneled();

        if (channeled instanceof Ability.IAttack) {
            int cooldown = data.getRemainingCooldown(channeled);

            if (cooldown > 0) {
                below.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.cooldown", JujutsuKaisen.MOD_ID), channeled.getName(), Math.round((float) cooldown / 20)));
            }
        }

        int belowY = aboveY + mc.font.lineHeight;

        for (Component line : below) {
            graphics.drawString(gui.getFont(), line, Math.round(20 * (1.0F / SCALE)), Math.round(belowY * (1.0F / SCALE)), 16777215);
            belowY += mc.font.lineHeight;
        }

        graphics.pose().popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    };
}
