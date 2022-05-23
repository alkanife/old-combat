package fr.alkanife.oldcombat.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import fr.alkanife.oldcombat.modules.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OldCombatCommand {

    public static void register() {
        CommandAPICommand attackSpeedCommand = new CommandAPICommand("attackspeed")
                .withArguments(new BooleanArgument("enable-disable"))
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;
                    AttackSpeedModule.ENABLED = (boolean) objects[0];
                    AttackSpeedModule.resetAll();
                    OldCombatCommand.broadcastOperators(commandSender.getName(), "AttackSpeed", "" + AttackSpeedModule.ENABLED);
                });

        CommandAPICommand criticalHitCommand = new CommandAPICommand("criticalHit")
                .withArguments(new BooleanArgument("enable-disable"))
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;
                    CriticalHitModule.ENABLED = (boolean) objects[0];
                    OldCombatCommand.broadcastOperators(commandSender.getName(), "CriticalHit", "" + CriticalHitModule.ENABLED);
                });

        CommandAPICommand knockbackCommand = new CommandAPICommand("knockback")
                .withArguments(new BooleanArgument("enable-disable"))
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;
                    KnockbackModule.ENABLED = (boolean) objects[0];
                    OldCombatCommand.broadcastOperators(commandSender.getName(), "Knockback", "" + KnockbackModule.ENABLED);
                });

        CommandAPICommand playerRegenCommand = new CommandAPICommand("playerregen")
                .withArguments(new BooleanArgument("enable-disable"))
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;
                    PlayerRegenModule.ENABLED = (boolean) objects[0];
                    OldCombatCommand.broadcastOperators(commandSender.getName(), "PlayerRegen", "" + PlayerRegenModule.ENABLED);
                });

        CommandAPICommand soundCommand = new CommandAPICommand("sound")
                .withArguments(new BooleanArgument("enable-disable"))
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;
                    SoundModule.ENABLED = (boolean) objects[0];
                    OldCombatCommand.broadcastOperators(commandSender.getName(), "Sound", "" + SoundModule.ENABLED);
                });

        CommandAPICommand sweepCommand = new CommandAPICommand("sweep")
                .withArguments(new BooleanArgument("enable-disable"))
                .executes((commandSender, objects) -> {
                    if (!commandSender.isOp())
                        return;
                    SweepModule.ENABLED = (boolean) objects[0];
                    OldCombatCommand.broadcastOperators(commandSender.getName(), "Sweep", "" + SweepModule.ENABLED);
                });

        new CommandAPICommand("oldcombat")
                .withSubcommand(attackSpeedCommand)
                .withSubcommand(criticalHitCommand)
                .withSubcommand(knockbackCommand)
                .withSubcommand(playerRegenCommand)
                .withSubcommand(soundCommand)
                .withSubcommand(sweepCommand)
                .executes((commandSender, objects) -> {
                    commandSender.sendMessage("§7-----");
                    commandSender.sendMessage("§7AttackSpeed module: " + AttackSpeedModule.ENABLED);
                    commandSender.sendMessage("§7CriticalHit module: " + CriticalHitModule.ENABLED);
                    commandSender.sendMessage("§7Knockback module: " + KnockbackModule.ENABLED);
                    commandSender.sendMessage("§7PlayerRegen module: " + PlayerRegenModule.ENABLED);
                    commandSender.sendMessage("§7Sound module: " + SweepModule.ENABLED);
                    commandSender.sendMessage("§7Sweep module: " + SweepModule.ENABLED);
                    commandSender.sendMessage("§7-----");
        }).register();
    }

    private static void broadcastOperators(String operator, String module, String state) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.isOp())
                player.sendMessage("§7§o[" + operator + ": Set " + module + " module to " + state + "]");
    }
}
