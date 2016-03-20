/*
 * Copyright 2015 josesousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.josesousa9000.easywarp;

import io.github.josesousa9000.easywarp.commands.WarpCommands;
import io.github.josesousa9000.easywarp.warps.Warps;
import java.io.File;
import java.io.IOException;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyWarp extends JavaPlugin {

    private WarpCommands commandExecutor;
    private Economy economy;
    private Permission permission;
    private Warps warps;

    public EasyWarp() {
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        permission = rsp.getProvider();
        return permission != null;
    }

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            String account = this.getConfig().getString("account");
            warps = new Warps(new File(getDataFolder(), "warps.json"));
            if (!setupEconomy() || !this.getConfig().getBoolean("economy")) {
                getLogger().info("EasyWarp not using economy!");
            }
            if (!setupPermissions()) {
                getLogger().info("EasyWarp not using permissions!");
            }
            this.commandExecutor = new WarpCommands(warps, economy, permission, account);
        } catch (IOException ex) {
            getLogger().info("Couldn't open warps.yml! Starting anew!");
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        boolean result = false;
        if (cmd.getName().equals("setwarp")) {
            result = this.commandExecutor.setWarp(args, sender);
        }
        if (cmd.getName().equals("warp")) {
            result = this.commandExecutor.useWarp(args, sender);
        }
        if (cmd.getName().equals("listwarp")) {
            result = this.commandExecutor.listWarp(args, sender);
        }
        if (cmd.getName().equals("delwarp")) {
            result = this.commandExecutor.deleteWarp(args, sender);
        }
        if (cmd.getName().equals("warpbank") && (sender instanceof Player)) {
            result = this.commandExecutor.changeBankAccount(args, sender);
        }
        return result;
    }
}
