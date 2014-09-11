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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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

            File permissions_yml = new File("plugins/PermissionsEx/permissions.yml");
            if (!permissions_yml.exists()) {
                  logMessage("No permission file found.");
                  return;
            }

            FileConfiguration perms_file = YamlConfiguration.loadConfiguration(permissions_yml);

            HashMap<String, List<String>> final_users = new HashMap<>();
            for (String user_name : perms_file.getConfigurationSection("users").getKeys(false)) {
                  logMessage("Cleaning user " + user_name);

                  List<String> user_groups = perms_file.getStringList("users." + user_name + ".group");
                  if (user_groups != null) {
                        for (String group_name : new ArrayList<>(user_groups)) {
                              if (!donor_ranks.contains(group_name.toLowerCase())) {
                                    user_groups.remove(group_name);
                              }
                        }
                  }
                  if (user_groups != null && !user_groups.isEmpty()) {
                        final_users.put(user_name, user_groups);
                  }
            }

            perms_file.set("users", null);
            
            for (Entry<String, List<String>> user : final_users.entrySet()) {
                  perms_file.set("users." + user.getKey() + ".group", user.getValue());
            }

            try {
                  perms_file.save(permissions_yml);
            } catch (IOException ex) {
                  ex.printStackTrace();
            }

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
