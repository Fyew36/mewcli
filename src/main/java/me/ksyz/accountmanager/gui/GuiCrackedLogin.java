package me.ksyz.accountmanager.gui;

import me.ksyz.accountmanager.auth.SessionManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

public class GuiCrackedLogin extends GuiScreen {
    private final GuiScreen previousScreen;
    private String status = "Cracked Login";
    private GuiTextField usernameField;

    public GuiCrackedLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        ScaledResolution sr = new ScaledResolution(mc);

        usernameField = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2, 200, 20);
        usernameField.setMaxStringLength(16);
        usernameField.setFocused(true);

        buttonList.add(new GuiButton(998, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 30, 200, 20, "Login"));

        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        mc.fontRendererObj.drawString(status, sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(status) / 2, sr.getScaledHeight() / 2 - 30, Color.WHITE.getRGB());
        usernameField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private ScaledResolution sr;

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 998) {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                status = "§cEnter a username";
                return;
            }
            if (username.length() > 16) {
                status = "§cUsername too long (max 16)";
                return;
            }
            String uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(java.nio.charset.StandardCharsets.UTF_8))
                    .toString().replace("-", "");
            SessionManager.set(new Session(username, uuid, "0", "legacy"));
            mc.displayGuiScreen(previousScreen);
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        usernameField.textboxKeyTyped(typedChar, keyCode);
        if (Keyboard.KEY_ESCAPE == keyCode) mc.displayGuiScreen(previousScreen);
        else super.keyTyped(typedChar, keyCode);
    }
}
