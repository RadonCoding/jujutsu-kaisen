package radon.jujutsu_kaisen.client.gui.overlay;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.imbuement.ImbuementHandler;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.CuriosUtil;

import java.util.ArrayList;
import java.util.List;

public class CursedEnergyOverlay {
    public static ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/overlay/energy_bar.png");

    private static final float SCALE = 0.6F;

    public static LayeredDraw.Layer OVERLAY = (pGuiGraphics, pPartialTick) -> {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();
        IIdleTransfigurationData idleTransfigurationData = cap.getIdleTransfigurationData();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(SCALE, SCALE, SCALE);

        List<Component> above = new ArrayList<>();

        if (!sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
            above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.output_boost", JujutsuKaisen.MOD_ID), Math.round(sorcererData.getOutputBoost() * 100.0F)));
            above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.output", JujutsuKaisen.MOD_ID), Math.round(sorcererData.getOutput() * 100)));
        }

        above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.experience", JujutsuKaisen.MOD_ID), sorcererData.getExperience()));

        if (!sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
            if (sorcererData.hasActiveTechnique(JJKCursedTechniques.IDLE_TRANSFIGURATION.get())) {
                above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.transfigured_souls", JujutsuKaisen.MOD_ID),
                        idleTransfigurationData.getTransfiguredSouls()));
            }
        }

        int aboveY = 26;

        for (Component line : above) {
            pGuiGraphics.drawString(mc.font, line, Math.round(20 * (1.0F / SCALE)), Math.round(aboveY * (1.0F / SCALE)), 16777215);
            aboveY += mc.font.lineHeight - 1;
        }
        pGuiGraphics.pose().popPose();

        if (sorcererData.getEnergy() > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Vector3f color = sorcererData.isInZone() ? ParticleColors.BLACK_FLASH : Vec3.fromRGB24(sorcererData.getCursedEnergyColor()).toVector3f();
            RenderSystem.setShaderColor(color.x, color.y, color.z, 1.0F);

            pGuiGraphics.blit(TEXTURE, 20, aboveY, 0, 0, 93, 10, 93, 18);

            float energyWidth = (sorcererData.getEnergy() / sorcererData.getMaxEnergy()) * 93.0F;
            pGuiGraphics.blit(TEXTURE, 20, aboveY + 1, 0, 10, (int) energyWidth, 8, 93, 18);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().scale(SCALE, SCALE, SCALE);

        if (sorcererData.getEnergy() > 0.0F) {
            pGuiGraphics.drawString(mc.font, String.format("%.1f / %.1f", sorcererData.getEnergy(), sorcererData.getMaxEnergy()),
                    Math.round(23 * (1.0F / SCALE)), Math.round((aboveY + 3.0F) * (1.0F / SCALE)), 16777215);
            aboveY += 4;
        }

        List<Component> below = new ArrayList<>();

        int burnout = sorcererData.getBurnout();

        if (burnout > 0) {
            below.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.burnout", JujutsuKaisen.MOD_ID), Math.round((float) burnout / 20)).withStyle(ChatFormatting.DARK_RED));
        }

        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(mc.player.getItemInHand(InteractionHand.MAIN_HAND));
        stacks.add(mc.player.getItemInHand(InteractionHand.OFF_HAND));
        stacks.addAll(CuriosUtil.findSlots(mc.player, "right_hand"));
        stacks.addAll(CuriosUtil.findSlots(mc.player, "left_hand"));

        stacks.removeIf(ItemStack::isEmpty);

        for (ItemStack stack : stacks) {
            for (Ability ability : ImbuementHandler.getFullImbuements(stack)) {
                int cooldown = abilityData.getRemainingCooldown(ability);

                if (cooldown > 0) {
                    below.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.cooldown", JujutsuKaisen.MOD_ID), ability.getName(), Math.round((float) cooldown / 20)));
                }
            }
        }

        for (Ability ability : abilityData.getToggled()) {
            if (!(ability instanceof IAttack)) continue;

            int cooldown = abilityData.getRemainingCooldown(ability);

            if (cooldown > 0) {
                below.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.cooldown", JujutsuKaisen.MOD_ID), ability.getName(), Math.round((float) cooldown / 20)));
            }
        }

        Ability channeled = abilityData.getChanneled();

        if (channeled instanceof IAttack) {
            int cooldown = abilityData.getRemainingCooldown(channeled);

            if (cooldown > 0) {
                below.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.cooldown", JujutsuKaisen.MOD_ID), channeled.getName(), Math.round((float) cooldown / 20)));
            }
        }

        int belowY = aboveY + mc.font.lineHeight;

        for (Component line : below) {
            pGuiGraphics.drawString(mc.font, line, Math.round(20 * (1.0F / SCALE)), Math.round(belowY * (1.0F / SCALE)), 16777215);
            belowY += mc.font.lineHeight;
        }

        pGuiGraphics.pose().popPose();
    };
}
