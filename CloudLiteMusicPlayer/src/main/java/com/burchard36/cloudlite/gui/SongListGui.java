package com.burchard36.cloudlite.gui;

import com.burchard36.cloudlite.CloudLiteMusicPlayer;
import com.burchard36.cloudlite.MusicPlayer;
import com.burchard36.cloudlite.config.SongData;
import com.burchard36.cloudlite.gui.buttons.InventoryButton;
import com.burchard36.cloudlite.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.burchard36.cloudlite.utils.StringUtils.convert;

public class SongListGui extends PaginatedInventory {
    protected final CloudLiteMusicPlayer moduleInstance;
    protected final GuiManager guiManager;
    protected final MusicPlayer musicPlayer;

    public SongListGui(CloudLiteMusicPlayer moduleInstance) {
        this.moduleInstance = moduleInstance;
        this.musicPlayer = this.moduleInstance.getMusicPlayer();
        this.guiManager = moduleInstance.getPluginInstance().getGuiManager();
        int totalPages = musicPlayer.getMusicConfig().getSongDataList().size() / 45;
        if (totalPages == 0) totalPages = 1;

        for (int currentPage = 0; currentPage < totalPages; currentPage++) {
            final int finalCurrentPage = currentPage;
            int finalTotalPages = totalPages;
            this.addPage(finalCurrentPage, new InventoryGui() {
                @Override
                public Inventory createInventory() {
                    return Bukkit.createInventory(
                            null,
                            54,
                            convert("&3&lSong List Page %s").formatted(finalCurrentPage));
                }

                @Override
                public void onInventoryClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    super.onInventoryClick(event);
                }

                @Override
                public void fillButtons() {
                    int startAt = finalCurrentPage * 45; // only display 45 songs per page
                    for (int x = 0; x < musicPlayer.getMusicConfig().getSongDataList().size(); x++) {
                        if (startAt > x) continue;

                        final SongData songData = musicPlayer.getMusicConfig().getSongDataList().get(x);

                        this.addButton(x, new InventoryButton(songData.getDisplayItem()) {
                            @Override
                            public void onClick(InventoryClickEvent clickEvent) {
                                musicPlayer.forcePlaySongFor((Player) clickEvent.getWhoClicked(), songData);
                            }
                        });
                    }

                    this.addButton(46, backgroundItem());
                    this.addButton(47, backgroundItem());
                    this.addButton(48, backgroundItem());
                    this.addButton(50, backgroundItem());
                    this.addButton(51, backgroundItem());
                    this.addButton(52, backgroundItem());
                    this.addButton(53, new InventoryButton(getNextButton()) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            final Player player = (Player) clickEvent.getWhoClicked();
                            int nextPage = finalCurrentPage + 1;
                            if (finalTotalPages == 1 || nextPage >= finalTotalPages) {
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                                player.sendMessage(convert("&cThis is the final page"));
                                return;
                            }

                            player.closeInventory();
                            guiManager.openPaginatedTo(player, nextPage, SongListGui.this);
                        }
                    });
                    this.addButton(45, new InventoryButton(getPreviousButton()) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            final Player player = (Player) clickEvent.getWhoClicked();
                            int previousPage = finalCurrentPage - 1;
                            if (finalTotalPages == 1 || previousPage < 0) {
                                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                                player.sendMessage(convert("&cThis is the first page"));
                                return;
                            }

                            player.closeInventory();
                            guiManager.openPaginatedTo(player, previousPage, SongListGui.this);
                        }
                    });

                    this.addButton(49, new InventoryButton(getNextSongButton()) {
                        @Override
                        public void onClick(InventoryClickEvent clickEvent) {
                            musicPlayer.playFor((Player) clickEvent.getWhoClicked());
                        }
                    });

                    super.fillButtons(); // actually sets them in the inventory
                }

            });
        }
    }

    private InventoryButton backgroundItem() {
        return new InventoryButton(ItemUtils.createItemStack(Material.CYAN_STAINED_GLASS_PANE, "&f ", null)) {
            @Override
            public void onClick(InventoryClickEvent clickEvent) {

            }
        };
    }

    private ItemStack getNextButton() {
        return ItemUtils.createSkull("ff9e19e5f2ce3488c29582b6d2601500626e8db2a88cd18164432fef2e34de6b", "&a&lNEXT PAGE", null);
    }
    private ItemStack getPreviousButton() {
        return ItemUtils.createSkull("f006ec1eca2f2685f70e65411cfe8808a088f7cf08087ad8eece9618361070e3", "&a&lPREVIOUS PAGE", null);
    }

    private ItemStack getNextSongButton() {
        return ItemUtils.createSkull("f0a606361ca311961de49a7d0b977108ff33d717ba13dfa8b70ec09cd7512c86", "&b&lPLAY NEXT SONG", null);
    }
}
