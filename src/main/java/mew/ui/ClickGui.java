package mew.ui;

import mew.Mew;
import mew.module.Module;
import mew.property.Property;
import mew.property.properties.*;
import mew.util.shader.BlurUtils;
import mew.util.shader.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClickGui extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private ScaledResolution sr;

    private static final int SIDEBAR_WIDTH = 90;
    private static final int MODULE_AREA_X = SIDEBAR_WIDTH + 4;
    private static final int PANEL_PADDING = 4;
    private static final int TAB_HEIGHT = 24;
    private static final int ROW_HEIGHT = 18;
    private static final int SETTING_HEIGHT = 14;
    private static final Color BG_COLOR = new Color(18, 18, 24, 200);
    private static final Color OUTLINE_COLOR = new Color(60, 70, 120, 180);
    private static final Color TAB_ACTIVE = new Color(40, 50, 100, 200);
    private static final Color TAB_INACTIVE = new Color(25, 28, 40, 180);
    private static final Color TEXT_COLOR = new Color(200, 200, 210);
    private static final Color TEXT_DIM = new Color(130, 130, 150);
    private static final Color ENABLED_COLOR = new Color(70, 140, 255);
    private static final Color DISABLED_COLOR = new Color(60, 60, 80);

    private final List<String> categories;
    private int selectedCategory;
    private float scrollOffset;
    private int prevMouseX, prevMouseY;
    private boolean dragging;
    private int dragStartX, dragStartY;
    private int windowX, windowY;
    private int windowW, windowH;

    public ClickGui() {
        this.categories = new ArrayList<>();
        this.categories.add("Combat");
        this.categories.add("Movement");
        this.categories.add("Render");
        this.categories.add("Player");
        this.categories.add("Misc");
        this.categories.add("Latency");
        this.categories.add("Minigames");
        this.categories.add("Target");
        this.selectedCategory = 0;
        this.scrollOffset = 0;
        this.windowX = 20;
        this.windowY = 20;
        this.windowW = 300;
        this.windowH = 350;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.sr = new ScaledResolution(mc);
        this.windowW = Math.min(360, sr.getScaledWidth() - 40);
        this.windowH = Math.min(400, sr.getScaledHeight() - 40);
        this.windowX = (sr.getScaledWidth() - windowW) / 2;
        this.windowY = (sr.getScaledHeight() - windowH) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.sr = new ScaledResolution(mc);

        BlurUtils.prepareBlur();
        RoundedUtils.drawRound(0, 0, width, height, 0, true, Color.BLACK);
        BlurUtils.blurEnd(2, 3);

        drawRect(0, 0, width, height, new Color(0, 0, 0, 120).getRGB());

        int sidebarEndX = SIDEBAR_WIDTH;
        int panelEndX = windowX + windowW;
        int panelEndY = windowY + windowH;
        int contentY = windowY + PANEL_PADDING;
        int contentEndX = panelEndX - PANEL_PADDING;
        int contentEndY = panelEndY - PANEL_PADDING;

        RoundedUtils.drawRound(windowX, windowY, windowW, windowH, 4, true, BG_COLOR);
        RoundedUtils.drawRoundOutline(windowX, windowY, windowW, windowH, 4, 1, BG_COLOR, OUTLINE_COLOR);

        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        int scale = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (windowX + PANEL_PADDING) * scale,
            (height - (windowY + PANEL_PADDING + (windowH - 2 * PANEL_PADDING))) * scale,
            (windowW - 2 * PANEL_PADDING) * scale,
            (windowH - 2 * PANEL_PADDING) * scale
        );

        float moduleY = contentY + 2 - scrollOffset;
        float moduleX = windowX + PANEL_PADDING + 2;

        String activeCat = categories.get(selectedCategory);
        List<Module> catModules = getModulesForCategory(activeCat);
        catModules.sort(Comparator.comparing(m -> m.getName().toLowerCase()));

        int visibleRows = 0;
        for (Module mod : catModules) {
            float rowY = moduleY + visibleRows * (ROW_HEIGHT + 2);
            if (rowY + ROW_HEIGHT > contentY && rowY < contentEndY) {
                boolean hovered = mouseX >= moduleX && mouseX <= contentEndX - 4
                    && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT;
                boolean enabled = mod.isEnabled();

                Color rowBg = enabled ? new Color(30, 35, 55, 200) : new Color(22, 25, 38, 200);
                if (hovered) rowBg = enabled ? new Color(40, 48, 72, 220) : new Color(32, 36, 52, 220);

                RoundedUtils.drawRound(moduleX, rowY, contentEndX - 4 - moduleX, ROW_HEIGHT, 3, true, rowBg);

                String name = mod.getName();
                int textColor = enabled ? ENABLED_COLOR.getRGB() : TEXT_COLOR.getRGB();
                mc.fontRendererObj.drawString(name, (int) moduleX + 6, (int) rowY + 5, textColor);

                if (hovered) {
                    String status = enabled ? "ON" : "OFF";
                    mc.fontRendererObj.drawString(status,
                        (int) (contentEndX - 4 - mc.fontRendererObj.getStringWidth(status) - 6),
                        (int) rowY + 5, enabled ? ENABLED_COLOR.getRGB() : TEXT_DIM.getRGB());
                }
            }
            visibleRows++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopAttrib();

        for (int i = 0; i < categories.size(); i++) {
            int tabX = windowX + 2;
            int tabY = windowY + PANEL_PADDING + 2 + i * (TAB_HEIGHT + 2);
            int tabW = SIDEBAR_WIDTH - 8;
            boolean selected = i == selectedCategory;
            boolean tabHovered = mouseX >= tabX && mouseX <= tabX + tabW
                && mouseY >= tabY && mouseY <= tabY + TAB_HEIGHT;

            Color tabBg = selected ? TAB_ACTIVE : (tabHovered ? new Color(32, 36, 52, 200) : TAB_INACTIVE);
            RoundedUtils.drawRound(tabX, tabY, tabW, TAB_HEIGHT, 3, true, tabBg);
            mc.fontRendererObj.drawString(categories.get(i),
                tabX + (tabW - mc.fontRendererObj.getStringWidth(categories.get(i))) / 2,
                tabY + (TAB_HEIGHT - mc.fontRendererObj.FONT_HEIGHT) / 2,
                selected ? ENABLED_COLOR.getRGB() : TEXT_COLOR.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = 0; i < categories.size(); i++) {
            int tabX = windowX + 2;
            int tabY = windowY + PANEL_PADDING + 2 + i * (TAB_HEIGHT + 2);
            int tabW = SIDEBAR_WIDTH - 8;
            if (mouseX >= tabX && mouseX <= tabX + tabW
                && mouseY >= tabY && mouseY <= tabY + TAB_HEIGHT) {
                selectedCategory = i;
                scrollOffset = 0;
                return;
            }
        }

        int contentEndX = windowX + windowW - PANEL_PADDING;
        int contentY = windowY + PANEL_PADDING;
        float moduleX = windowX + PANEL_PADDING + 2;

        String activeCat = categories.get(selectedCategory);
        List<Module> catModules = getModulesForCategory(activeCat);
        catModules.sort(Comparator.comparing(m -> m.getName().toLowerCase()));

        int idx = 0;
        for (Module mod : catModules) {
            float rowY = contentY + 2 - scrollOffset + idx * (ROW_HEIGHT + 2);
            if (mouseX >= moduleX && mouseX <= contentEndX - 4
                && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT) {
                if (mouseButton == 0) {
                    mod.toggle();
                }
                return;
            }
            idx++;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            scrollOffset = Math.max(0, scrollOffset - scroll / 120f * 20);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private List<Module> getModulesForCategory(String category) {
        List<Module> result = new ArrayList<>();
        for (Module mod : Mew.moduleManager.modules.values()) {
            if (category.equalsIgnoreCase(mod.getCategory())) {
                result.add(mod);
            }
        }
        return result;
    }
}
