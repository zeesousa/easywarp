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
package io.github.josesousa9000.easywarp.warps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpsAPI {

  private final boolean usePlayerName;
  private Map<String, PlayerWarps> warps;
  private final File warpsFile;
  private final Serializer serializer;

  public WarpsAPI(File file, boolean usePlayerName) throws IOException {
    this.usePlayerName = usePlayerName;
    this.warps = new HashMap<>();
    this.warpsFile = file;
    this.serializer = new Serializer();
    this.warps = this.serializer.loadFromFile(file);
  }

  private PlayerWarps getLocations(Player player) {
    String playerID;
    if (usePlayerName) {
      playerID = player.getName();
    } else {
      playerID = player.getUniqueId().toString();
    }
    PlayerWarps locations = this.warps.get(playerID);
    if (locations == null) {
      locations = new PlayerWarps();
      warps.put(playerID, locations);
    }
    return locations;
  }

  public void setWarp(Player player, String name, Location l) {
    PlayerWarps locations = this.getLocations(player);
    locations.addWarp(name, l);
    this.serializer.saveToFile(this.warps, warpsFile);
  }

  public Location getWarp(Player player, String name) {
    PlayerWarps locations = this.getLocations(player);
    return locations.getWarp(name);
  }

  public boolean delWarp(Player player, String name) {
    PlayerWarps locations = this.getLocations(player);
    boolean val = locations.deleteWarp(name);
    this.serializer.saveToFile(this.warps, warpsFile);
    return val;
  }

  public void listWarps(Player player) {
    PlayerWarps locations = this.getLocations(player);
    player.sendMessage("[EasyWarp] Your memes:");
    for (Entry<String, Location> entry : locations.entrySet()) {
      sendWarpInfo(player, entry);
    }

  }

  public void listWarps(Player player, int page) {
    PlayerWarps locations = this.getLocations(player);
    player.sendMessage(String.format("[EasyWarp] Page %d of your memes:%n", page));
    List entries = new ArrayList(locations.entrySet());
    int start = 5 * (page - 1);
    for (int i = start; i < start + 5 && i < entries.size(); i++) {
      java.util.Map.Entry entry = (java.util.Map.Entry) entries.get(i);
      sendWarpInfo(player, entry);
    }

  }

  public void listWarps(Player player, String prefix) {
    PlayerWarps locations = this.getLocations(player);
    player.sendMessage("[EasyWarp] Your memes:");
    for (Entry<String, Location> entry : locations.entrySet()) {
      if (entry.getKey().startsWith(prefix)) {
        sendWarpInfo(player, entry);
      }
    }
  }

  public List<String> getWarpsByPrefix(Player player, String prefix) {
    PlayerWarps locations = this.getLocations(player);
    List<String> warpList = new LinkedList<>();
    for (Entry<String, Location> entry : locations.entrySet()) {
      if (entry.getKey().startsWith(prefix)) {
        warpList.add(entry.getKey());
      }
    }
    return warpList;
  }

  private void sendWarpInfo(Player player, Entry<String, Location> entry) {
    Location l = entry.getValue();
    String message = String.format("%s Name:%s%s | %sWorld: %s %s| X: %d | Y: %d | Z: %d%n",
            ChatColor.GREEN, ChatColor.WHITE, entry.getKey(), ChatColor.GREEN, l.getWorld().getName(), ChatColor.WHITE, l.getBlockX(), l.getBlockY(), l.getBlockZ());
    player.sendMessage(message);
  }
}
