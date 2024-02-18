package radon.jujutsu_kaisen.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.menu.VeilRodMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetVeilSizeC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.ToggleVeilC2SPacket;

public class VeilRodScreen extends AbstractContainerScreen<VeilRodMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/container/veil_rod.png");

    private ExtendedSlider sizeSlider;

    private Button activate;
    private Button deactivate;

    private int oldSize;

    public VeilRodScreen(VeilRodMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.menu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(@NotNull AbstractContainerMenu pContainerToSend, int pDataSlotIndex, @NotNull ItemStack pStack) {

            }

            @Override
            public void dataChanged(@NotNull AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
                VeilRodScreen.this.sizeSlider.setValue(VeilRodScreen.this.menu.getSize());
                VeilRodScreen.this.activate.active = !VeilRodScreen.this.menu.isActive();
                VeilRodScreen.this.deactivate.active = VeilRodScreen.this.menu.isActive();
            }
        });
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        int newSize = this.sizeSlider.getValueInt();

        if (this.oldSize > 0) {
            if (newSize != this.oldSize) {
                PacketHandler.sendToServer(new SetVeilSizeC2SPacket(newSize));
            }
        }
        this.oldSize = newSize;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.sizeSlider.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) && super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.sizeSlider = new ExtendedSlider(i + 33, j + 29, 110, 16, Component.empty(), Component.empty(),
                ConfigHolder.SERVER.minimumVeilSize.get(), ConfigHolder.SERVER.maximumVeilSize.get(), this.menu.getSize(), true);
        this.addRenderableWidget(this.sizeSlider);
        this.setInitialFocus(this.sizeSlider);

        this.activate = this.addRenderableWidget(ExtendedButton.builder(Component.translatable(String.format("gui.%s.veil_rod.activate", JujutsuKaisen.MOD_ID)), ignored -> {
            PacketHandler.sendToServer(new ToggleVeilC2SPacket(true));
        }).pos(i + 33, j + 47).size(54, 16).build());
        this.deactivate = this.addRenderableWidget(ExtendedButton.builder(Component.translatable(String.format("gui.%s.veil_rod.deactivate", JujutsuKaisen.MOD_ID)), ignored -> {
            PacketHandler.sendToServer(new ToggleVeilC2SPacket(false));
        }).pos(i + 89, j + 47).size(54, 16).build());
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
