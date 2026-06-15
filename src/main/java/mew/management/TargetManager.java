package mew.management;

import mew.enums.ChatColors;

import java.awt.*;
import java.io.File;

public class TargetManager extends PlayerFileManager {
    public TargetManager() {
        super(new File("./config/mewcli/", "enemies.txt"), new Color(ChatColors.DARK_RED.toAwtColor()));
    }
}
