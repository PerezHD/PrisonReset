/*
 * Copyright (C) 2014 Harry Devane
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.volcanicpvp.prisonreset;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * https://www.github.com/Harry5573OP
 *
 * @author Harry5573OP
 */
public class PrisonReset extends JavaPlugin {

      @Getter
      private static PrisonReset plugin_instance = null;

      private HashSet<String> donor_ranks = new HashSet<>();

      @Override
      public void onEnable() {
            plugin_instance = this;

            logMessage("==[ Plugin version " + getDescription().getVersion() + " starting ]==");

            saveDefaultConfig();
            for (String donor_rank : getConfig().getStringList("donor_ranks")) {
                  logMessage("Loaded donator rank " + donor_rank + " we will not remove this rank from users.");
                  donor_ranks.add(donor_rank.toLowerCase());
            }

            logMessage("Beginning user clean");

            final PermissionsEx perms_plugin = (PermissionsEx) Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");

            getServer().dispatchCommand(getServer().getConsoleSender(), "whitelist on");
            getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                  public void run() {
                        Set<PermissionUser> users = perms_plugin.getPermissionsManager().getUsers();

                        for (PermissionUser user : users) {
                              for (final PermissionGroup group : user.getGroups()) {
                                    if (!donor_ranks.contains(group.getName().toLowerCase())) {
                                          user.removeGroup(group);
                                          logMessage("[CLEANER] Removed " + group.getName() + " from user " + user.getName());
                                    }
                              }

                              if (user.getGroups().length > 0) {
                                    user.save();
                              } else {
                                    user.remove();
                              }
                        }
                  }
            }, 40L);

            logMessage("==[ Plugin version " + getDescription().getVersion() + " started ]==");
      }

      @Override
      public void onDisable() {
            logMessage("==[ Plugin version " + getDescription().getVersion() + " stopping ]==");

            logMessage("==[ Plugin version " + getDescription().getVersion() + " shutdown ]==");
      }

      private void logMessage(String message) {
            getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[" + getName() + "] " + ChatColor.WHITE + message);
      }
}
