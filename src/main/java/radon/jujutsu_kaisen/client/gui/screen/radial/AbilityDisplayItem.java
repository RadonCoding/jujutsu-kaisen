package radon.jujutsu_kaisen.client.gui.screen.radial;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.ability.cursed_speech.ICursedSpeech;
import radon.jujutsu_kaisen.ability.idle_transfiguration.base.ITransfiguredSoul;
import radon.jujutsu_kaisen.client.util.RenderUtil;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

import java.util.ArrayList;
import java.util.List;

public class AbilityDisplayItem extends DisplayItem {
    private static final ResourceLocation HEALTH_BAR = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/overlay/health_bar.png");
    private static final float HEALTH_BAR_SCALE = 0.3F;

    private final Ability ability;

    public AbilityDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, Ability ability) {
        super(minecraft, screen, select);

        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }

    @Override
    public void drawHover(GuiGraphics graphics, int x, int y) {
        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        List<Component> lines = new ArrayList<>();

        float cost = this.ability.getRealCost(this.minecraft.player);

        if (cost > 0.0F) {
            lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID), cost));
        }

        int remaining = data.getRemainingCooldown(this.ability);
        int cooldown = remaining > 0 ? remaining : this.ability.getRealCooldown(this.minecraft.player);

        if (cooldown > 0) {
            lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cooldown", JujutsuKaisen.MOD_ID), Math.round((float) cooldown / 20)));
        }

        // TODO: Have a IHoverable interface or something like that, which provides additional hover text

        if (this.ability instanceof IDurationable durationable) {
            int duration = durationable.getRealDuration(this.minecraft.player);

            if (duration > 0) {
                Component durationText = Component.translatable(String.format("gui.%s.ability_overlay.duration", JujutsuKaisen.MOD_ID), (float) duration / 20);
                lines.add(durationText);
            }
        }

        if (this.ability instanceof ITransfiguredSoul soul) {
            Component soulCostText = Component.translatable(String.format("gui.%s.ability_overlay.soul_cost", JujutsuKaisen.MOD_ID), soul.getSoulCost());
            lines.add(soulCostText);
        }

        if (this.ability instanceof ICursedSpeech speech) {
            Component throatDamageText = Component.translatable(String.format("gui.%s.ability_overlay.throat_damage", JujutsuKaisen.MOD_ID), speech.getThroatDamage());
            lines.add(throatDamageText);
        }

        for (Component line : lines) {
            graphics.drawCenteredString(this.minecraft.font, line, x, y - ((lines.size() - 1) * this.minecraft.font.lineHeight), 0xFFFFFF);
            y += this.minecraft.font.lineHeight;
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        if (this.minecraft.level == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (this.ability instanceof Summon<?> summon && summon.isDisplayed()) {
            List<EntityType<?>> types = summon.getTypes();

            float height = 0.0F;
            int scale = 0;

            for (EntityType<?> type : types) {
                if (!(type.create(this.minecraft.level) instanceof LivingEntity instance)) continue;

                height = Math.max(height, instance.getBbHeight());
                scale = Math.max(scale, Math.round(Math.max(3.0F, 10.0F - instance.getBbHeight())));
            }

            for (EntityType<?> type : types) {
                if (!(type.create(this.minecraft.level) instanceof LivingEntity instance)) continue;

                RenderUtil.renderEntityInInventoryFollowsAngle(graphics, x, y,
                        scale, 0.0F, -1.0F, -0.5F, instance);
            }

            if (summon.getActivationType(this.minecraft.player) == Ability.ActivationType.TOGGLED) {
                int yOffset = Math.round((y + (height * scale / 2.0F) + (this.minecraft.font.lineHeight / 2.0F)) * (1.0F / HEALTH_BAR_SCALE));

                for (Entity entity : data.getSummonsByClass(summon.getClazz())) {
                    if (!(entity instanceof LivingEntity living)) continue;

                    graphics.pose().pushPose();
                    graphics.pose().scale(HEALTH_BAR_SCALE, HEALTH_BAR_SCALE, HEALTH_BAR_SCALE);

                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);

                    int xOffset = Math.round(x * (1.0F / HEALTH_BAR_SCALE) - 46.5F);

                    graphics.blit(HEALTH_BAR, xOffset, yOffset, 0, 0, 93, 10, 93, 18);

                    float health = (living.getHealth() / living.getMaxHealth()) * 93.0F;
                    graphics.blit(HEALTH_BAR, xOffset, yOffset + 1, 0, 10, (int) health, 8, 93, 18);

                    RenderSystem.depthMask(true);
                    RenderSystem.enableDepthTest();

                    graphics.pose().popPose();

                    yOffset += 10 + 2;
                }
            }
        } else {
            graphics.pose().pushPose();
            graphics.pose().scale(0.5F, 0.5F, 0.0F);
            graphics.pose().translate(x, y - (float) this.minecraft.font.lineHeight / 2, 0.0F);
            graphics.drawCenteredString(this.minecraft.font, this.ability.getName(), x, y - this.minecraft.font.lineHeight / 2, 0xFFFFFF);
            graphics.pose().popPose();
        }
    }

    @Override
    public void mouseClicked(int button) {

    }

    @Override
    public boolean isActive() {
        if (this.minecraft.player == null) return false;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        return data.hasToggled(this.ability);
    }
}