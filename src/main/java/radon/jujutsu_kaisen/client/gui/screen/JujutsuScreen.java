package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.screen.tab.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JujutsuScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;

    private final List<JJKTab> tabs = new ArrayList<>();

    @Nullable
    private JJKTab selectedTab;
    private static int tabPage, maxPages;

    private boolean isScrolling;

    public JujutsuScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    private void setSelectedTab(@NotNull JJKTab tab) {
        if (this.selectedTab != null) {
            for (GuiEventListener widget : this.selectedTab.getRenderables()) {
                this.removeWidget(widget);
            }
            this.selectedTab.removeWidgets();
        }
        this.selectedTab = tab;
        this.selectedTab.addWidgets();
    }

    public void removeWidgets() {
        if (this.selectedTab != null) {
            for (GuiEventListener widget : this.selectedTab.getRenderables()) {
                this.removeWidget(widget);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.selectedTab != null) {
            this.selectedTab.tick();
        }
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T pWidget) {
        return super.addRenderableWidget(pWidget);
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;

        int index = 0;
        this.tabs.add(new StatsTab(this.minecraft, this, JJKTabType.ABOVE, 0, 0));
        index++;
        this.tabs.add(new DomainCustomizationTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
        index++;
        this.tabs.add(new PactTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
        index++;
        this.tabs.add(new BindingVowTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
        index++;
        this.tabs.add(new AbilityTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
        index++;
        this.tabs.add(new SkillsTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
        index++;
        this.tabs.add(new ChantTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
        index++;
        this.tabs.add(new CursedEnergyColorTab(this.minecraft, this, JJKTabType.ABOVE, index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));

        this.setSelectedTab(this.tabs.get(0));

        if (this.tabs.size() > JJKTabType.MAX_TABS) {
            int guiLeft = (this.width - WINDOW_WIDTH) / 2;
            int guiTop = (this.height - WINDOW_HEIGHT) / 2;
            this.addRenderableWidget(Button.builder(Component.literal("<"), b -> tabPage = Math.max(tabPage - 1, 0))
                    .pos(guiLeft, guiTop - 50).size(20, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal(">"), b -> tabPage = Math.min(tabPage + 1, maxPages))
                    .pos(guiLeft + WINDOW_WIDTH - 20, guiTop - 50).size(20, 20).build());
            maxPages = this.tabs.size() / JJKTabType.MAX_TABS;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.selectedTab != null) {
            int i = (this.width - WINDOW_WIDTH) / 2;
            int j = (this.height - WINDOW_HEIGHT) / 2;
            this.selectedTab.mouseClicked(pMouseX - i - 9, pMouseY - j - 18, pButton);
        }

        if (pButton == 0) {
            int i = (this.width - WINDOW_WIDTH) / 2;
            int j = (this.height - WINDOW_HEIGHT) / 2;

            for (JJKTab tab : this.tabs) {
                if (tab.getPage() == tabPage && tab.isMouseOver(i, j, pMouseX, pMouseY)) {
                    this.setSelectedTab(tab);
                    break;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.selectedTab != null) {
            int i = (this.width - WINDOW_WIDTH) / 2;
            int j = (this.height - WINDOW_HEIGHT) / 2;
            this.selectedTab.mouseReleased(pMouseX - i - 9, pMouseY - j - 18, pButton);
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.selectedTab != null) {
            this.selectedTab.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - WINDOW_WIDTH) / 2;
        int j = (this.height - WINDOW_HEIGHT) / 2;
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (maxPages != 0) {
            net.minecraft.network.chat.Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
            int width = this.font.width(page);
            pGuiGraphics.drawString(this.font, page.getVisualOrderText(), i + (WINDOW_WIDTH / 2) - (width / 2), j - 44, -1);
        }
        this.renderInside(pGuiGraphics, pMouseX, pMouseY, i, j);
        this.renderWindow(pGuiGraphics, i, j);
        this.renderTooltips(pGuiGraphics, pMouseX, pMouseY, i, j);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderInside(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        if (this.selectedTab != null) {
            this.selectedTab.drawContents(pGuiGraphics, pOffsetX + WINDOW_INSIDE_X, pOffsetY + WINDOW_INSIDE_Y);
        }
    }

    public void renderWindow(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
        RenderSystem.enableBlend();
        pGuiGraphics.blit(WINDOW_LOCATION, pOffsetX, pOffsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        for (JJKTab tab : this.tabs) {
            if (tab.getPage() == tabPage) {
                tab.drawTab(pGuiGraphics, pOffsetX, pOffsetY, tab == this.selectedTab);
            }
        }

        for (JJKTab tab : this.tabs) {
            if (tab.getPage() == tabPage) {
                tab.drawIcon(pGuiGraphics, pOffsetX, pOffsetY);
            }
        }
    }

    private void renderTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        if (this.selectedTab != null) {
            this.selectedTab.drawTooltips(pGuiGraphics, pMouseX, pMouseY, pOffsetX, pOffsetY);
        }

        for (JJKTab JJKTab : this.tabs) {
            if (JJKTab.getPage() == tabPage && JJKTab.isMouseOver(pOffsetX, pOffsetY, pMouseX, pMouseY)) {
                pGuiGraphics.renderTooltip(this.font, JJKTab.getTitle(), pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.scroll(pDragX, pDragY);
            }
            return true;
        }
    }
}
