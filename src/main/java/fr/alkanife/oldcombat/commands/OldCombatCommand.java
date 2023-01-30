package fr.alkanife.oldcombat.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import fr.alkanife.oldcombat.modules.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OldCombatCommand {

    public static void register() {
        BooleanArgument booleanArgument = new BooleanArgument("enable-disable");

        CommandAPICommand attackSpeedCommand = new CommandAPICommand("attackspeed")
                .withArguments(booleanArgument)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    toggleAttackSpeed(commandSender, objects[0]);
                });

        CommandAPICommand criticalHitCommand = new CommandAPICommand("criticalhit")
                .withArguments(booleanArgument)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    toggleCriticalHit(commandSender, objects[0]);
                });

        CommandAPICommand knockbackCommand = new CommandAPICommand("knockback")
                .withArguments(booleanArgument)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    toggleKnockback(commandSender, objects[0]);
                });

        CommandAPICommand playerRegenCommand = new CommandAPICommand("playerregen")
                .withArguments(booleanArgument)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    togglePlayerRegen(commandSender, objects[0]);
                });

        CommandAPICommand soundCommand = new CommandAPICommand("sound")
                .withArguments(booleanArgument)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    toggleSound(commandSender, objects[0]);
                });

        CommandAPICommand sweepCommand = new CommandAPICommand("sweep")
                .withArguments(booleanArgument)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    toggleSweep(commandSender, objects[0]);
                });

        CommandAPICommand allEnable = new CommandAPICommand("enable").executes((commandSender, objects) -> {
            toggleAttackSpeed(commandSender, true);
            toggleCriticalHit(commandSender, true);
            toggleKnockback(commandSender, true);
            togglePlayerRegen(commandSender, true);
            toggleSound(commandSender, true);
            toggleSweep(commandSender, true);
        });

        CommandAPICommand allDisable = new CommandAPICommand("disable").executes((commandSender, objects) -> {
            toggleAttackSpeed(commandSender, false);
            toggleCriticalHit(commandSender, false);
            toggleKnockback(commandSender, false);
            togglePlayerRegen(commandSender, false);
            toggleSound(commandSender, false);
            toggleSweep(commandSender, false);
        });

        new CommandAPICommand("oldcombat")
                .withSubcommand(attackSpeedCommand)
                .withSubcommand(criticalHitCommand)
                .withSubcommand(knockbackCommand)
                .withSubcommand(playerRegenCommand)
                .withSubcommand(soundCommand)
                .withSubcommand(sweepCommand)
                .withSubcommand(allEnable)
                .withSubcommand(allDisable)
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;

                    String enable = "/oldcombat enable";
                    String disable = "/oldcombat disable";

                    commandSender.sendMessage("§f");
                    showToggle(commandSender, "AttackSpeed", AttackSpeedModule.ENABLED);
                    showToggle(commandSender, "CriticalHit", CriticalHitModule.ENABLED);
                    showToggle(commandSender, "Knockback", KnockbackModule.ENABLED);
                    showToggle(commandSender, "PlayerRegen", PlayerRegenModule.ENABLED);
                    showToggle(commandSender, "Sound", SweepModule.ENABLED);
                    showToggle(commandSender, "Sweep", SweepModule.ENABLED);
                    commandSender.sendMessage("§f");
                    commandSender.sendMessage(
                            Component.text("§7[Enable everything]")
                            .hoverEvent(HoverEvent.showText(Component.text(enable)))
                            .clickEvent(ClickEvent.runCommand(enable)).append(Component.text(" "))
                            .append(
                                    Component.text("§7[Disable everything]")
                                    .hoverEvent(HoverEvent.showText(Component.text(disable)))
                                    .clickEvent(ClickEvent.runCommand(disable))));
                    commandSender.sendMessage("§f");
        }).register();
    }

    private static void broadcastOperators(String operator, String module, String state) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.isOp())
                player.sendMessage("§7§o[" + operator + ": Set " + module + " module to " + state + "]");
    }

    private static void showToggle(CommandSender commandSender, String name, boolean current) {
        commandSender.sendMessage(Component.text("§7 - " + name + " module: " + current)
                .hoverEvent(HoverEvent.showText(Component.text("Click to " + (current ? "disable" : "enable") + " the " + name + " module")))
                .clickEvent(ClickEvent.runCommand("/oldcombat " + name.toLowerCase() + " " + !current)));
    }

    private static void toggleAttackSpeed(CommandSender commandSender, Object object) {
        AttackSpeedModule.ENABLED = (boolean) object;
        AttackSpeedModule.resetAll();
        OldCombatCommand.broadcastOperators(commandSender.getName(), "AttackSpeed", "" + AttackSpeedModule.ENABLED);
        commandSender.sendMessage("§6All connected players need to reconnect for the change to be effective.");
    }

    private static void toggleCriticalHit(CommandSender commandSender, Object object) {
        CriticalHitModule.ENABLED = (boolean) object;
        OldCombatCommand.broadcastOperators(commandSender.getName(), "CriticalHit", "" + CriticalHitModule.ENABLED);
    }

    private static void toggleKnockback(CommandSender commandSender, Object object) {
        KnockbackModule.ENABLED = (boolean) object;
        OldCombatCommand.broadcastOperators(commandSender.getName(), "Knockback", "" + KnockbackModule.ENABLED);
    }

    private static void togglePlayerRegen(CommandSender commandSender, Object object) {
        PlayerRegenModule.ENABLED = (boolean) object;
        OldCombatCommand.broadcastOperators(commandSender.getName(), "PlayerRegen", "" + PlayerRegenModule.ENABLED);
    }

    private static void toggleSound(CommandSender commandSender, Object object) {
        SoundModule.ENABLED = (boolean) object;
        OldCombatCommand.broadcastOperators(commandSender.getName(), "Sound", "" + SoundModule.ENABLED);
    }

    private static void toggleSweep(CommandSender commandSender, Object object) {
        SweepModule.ENABLED = (boolean) object;
        OldCombatCommand.broadcastOperators(commandSender.getName(), "Sweep", "" + SweepModule.ENABLED);
    }
}
