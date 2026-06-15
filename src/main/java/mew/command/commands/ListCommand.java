package mew.command.commands;

import mew.Mew;
import mew.command.Command;
import mew.module.Module;
import mew.util.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class ListCommand extends Command {
    public ListCommand() {
        super(new ArrayList<>(Arrays.asList("list", "l", "modules", "mewcli")));
    }

    @Override
    public void runCommand(ArrayList<String> args) {
        if (!Mew.moduleManager.modules.isEmpty()) {
            ChatUtil.sendFormatted(String.format("%sModules:&r", Mew.clientName));
            for (Module module : Mew.moduleManager.modules.values()) {
                ChatUtil.sendFormatted(String.format("%s»&r %s&r", module.isHidden() ? "&8" : "&7", module.formatModule()));
            }
        }
    }
}
