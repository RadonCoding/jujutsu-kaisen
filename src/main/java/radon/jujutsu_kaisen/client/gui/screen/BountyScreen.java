package radon.jujutsu_kaisen.client.gui.screen;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.c2s.RequestBountyCostC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetTojiBountyC2SPacket;

public class BountyScreen extends AbstractContainerScreen<BountyMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/container/bounty.png");

    private EditBox name;

    public BountyScreen(BountyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, i + 18, j + 29, 103, 12, Component.translatable(String.format("container.%s.bounty.target", JujutsuKaisen.MOD_ID)));
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(16);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addRenderableWidget(this.name);
        this.setInitialFocus(this.name);

        Button button = new Button.Builder(Component.translatable(String.format("container.%s.bounty.accept", JujutsuKaisen.MOD_ID)), ignored -> {
            if (this.menu.charge()) {
                PacketDistributor.sendToServer(new SetTojiBountyC2SPacket(this.name.getValue()));
                this.onClose();
            }
        }).pos(i + 59, j + 46).size(58, 16).build();
        this.addRenderableWidget(button);
    }

    private void onNameChanged(String name) {
        if (!name.isEmpty()) {
            PacketDistributor.sendToServer(new RequestBountyCostC2SPacket(name));
        }
    }

    @Override
    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        String name = this.name.getValue();
        this.init(pMinecraft, pWidth, pHeight);
        this.name.setValue(name);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        }
        return this.name.keyPressed(pKeyCode, pScanCode, pModifiers) || this.name.canConsumeInput() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void renderPlaceholderSlot(GuiGraphics graphics, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        String s = null;

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.minecraft != null && this.minecraft.player != null) {
            ItemStack display = new ItemStack(Items.EMERALD, this.menu.getCost());
            graphics.renderFakeItem(display, i, j);
            graphics.renderItemDecorations(this.font, display, i, j, s);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.pose().popPose();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        Slot slot = this.menu.slots.get(0);

        if (!slot.hasItem()) {
            int i = this.leftPos;
            int j = this.topPos;

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate((float) i, (float) j, 0.0F);

            this.renderPlaceholderSlot(pGuiGraphics, slot);

            pGuiGraphics.pose().popPose();
        }
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pX, int pY) {
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        pGuiGraphics.blit(TEXTURE, this.leftPos + 15, this.topPos + 25, 0, this.imageHeight, 110, 16);
    }
}
