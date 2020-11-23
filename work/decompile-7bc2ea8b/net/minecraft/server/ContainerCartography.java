package net.minecraft.server;

public class ContainerCartography extends Container {

    private final ContainerAccess containerAccess;
    private boolean e;
    private long f;
    public final IInventory inventory;
    private final InventoryCraftResult resultInventory;

    public ContainerCartography(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.a);
    }

    public ContainerCartography(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.CARTOGRAPHY_TABLE, i);
        this.inventory = new InventorySubcontainer(2) {
            @Override
            public void update() {
                ContainerCartography.this.a((IInventory) this);
                super.update();
            }
        };
        this.resultInventory = new InventoryCraftResult() {
            @Override
            public void update() {
                ContainerCartography.this.a((IInventory) this);
                super.update();
            }
        };
        this.containerAccess = containeraccess;
        this.a(new Slot(this.inventory, 0, 15, 15) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.getItem() == Items.FILLED_MAP;
            }
        });
        this.a(new Slot(this.inventory, 1, 15, 52) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                Item item = itemstack.getItem();

                return item == Items.PAPER || item == Items.MAP || item == Items.dP;
            }
        });
        this.a(new Slot(this.resultInventory, 2, 145, 39) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public ItemStack a(int j) {
                ItemStack itemstack = super.a(j);
                ItemStack itemstack1 = (ItemStack) containeraccess.a((world, blockposition) -> {
                    if (!ContainerCartography.this.e && ContainerCartography.this.inventory.getItem(1).getItem() == Items.dP) {
                        ItemStack itemstack2 = ItemWorldMap.a(world, ContainerCartography.this.inventory.getItem(0));

                        if (itemstack2 != null) {
                            itemstack2.setCount(1);
                            return itemstack2;
                        }
                    }

                    return itemstack;
                }).orElse(itemstack);

                ContainerCartography.this.inventory.splitStack(0, 1);
                ContainerCartography.this.inventory.splitStack(1, 1);
                return itemstack1;
            }

            @Override
            protected void a(ItemStack itemstack, int j) {
                this.a(j);
                super.a(itemstack, j);
            }

            @Override
            public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
                itemstack.getItem().b(itemstack, entityhuman.world, entityhuman);
                containeraccess.a((world, blockposition) -> {
                    long j = world.getTime();

                    if (ContainerCartography.this.f != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerCartography.this.f = j;
                    }

                });
                return super.a(entityhuman, itemstack);
            }
        });

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return a(this.containerAccess, entityhuman, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.inventory.getItem(0);
        ItemStack itemstack1 = this.inventory.getItem(1);
        ItemStack itemstack2 = this.resultInventory.getItem(2);

        if (!itemstack2.isEmpty() && (itemstack.isEmpty() || itemstack1.isEmpty())) {
            this.resultInventory.splitWithoutUpdate(2);
        } else if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            this.a(itemstack, itemstack1, itemstack2);
        }

    }

    private void a(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2) {
        this.containerAccess.a((world, blockposition) -> {
            Item item = itemstack1.getItem();
            WorldMap worldmap = ItemWorldMap.a(itemstack, world);

            if (worldmap != null) {
                ItemStack itemstack3;

                if (item == Items.PAPER && !worldmap.locked && worldmap.scale < 4) {
                    itemstack3 = itemstack.cloneItemStack();
                    itemstack3.setCount(1);
                    itemstack3.getOrCreateTag().setInt("map_scale_direction", 1);
                    this.c();
                } else if (item == Items.dP && !worldmap.locked) {
                    itemstack3 = itemstack.cloneItemStack();
                    itemstack3.setCount(1);
                    this.c();
                } else {
                    if (item != Items.MAP) {
                        this.resultInventory.splitWithoutUpdate(2);
                        this.c();
                        return;
                    }

                    itemstack3 = itemstack.cloneItemStack();
                    itemstack3.setCount(2);
                    this.c();
                }

                if (!ItemStack.matches(itemstack3, itemstack2)) {
                    this.resultInventory.setItem(2, itemstack3);
                    this.c();
                }

            }
        });
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.inventory != this.resultInventory && super.a(itemstack, slot);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.b;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            ItemStack itemstack2 = itemstack1;
            Item item = itemstack1.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (this.inventory.getItem(1).getItem() == Items.dP) {
                    itemstack2 = (ItemStack) this.containerAccess.a((world, blockposition) -> {
                        ItemStack itemstack3 = ItemWorldMap.a(world, this.inventory.getItem(0));

                        if (itemstack3 != null) {
                            itemstack3.setCount(1);
                            return itemstack3;
                        } else {
                            return itemstack1;
                        }
                    }).orElse(itemstack1);
                }

                item.b(itemstack2, entityhuman.world, entityhuman);
                if (!this.a(itemstack2, 3, 39, true)) {
                    return ItemStack.b;
                }

                slot.a(itemstack2, itemstack);
            } else if (i != 1 && i != 0) {
                if (item == Items.FILLED_MAP) {
                    if (!this.a(itemstack1, 0, 1, false)) {
                        return ItemStack.b;
                    }
                } else if (item != Items.PAPER && item != Items.MAP && item != Items.dP) {
                    if (i >= 3 && i < 30) {
                        if (!this.a(itemstack1, 30, 39, false)) {
                            return ItemStack.b;
                        }
                    } else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false)) {
                        return ItemStack.b;
                    }
                } else if (!this.a(itemstack1, 1, 2, false)) {
                    return ItemStack.b;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
                return ItemStack.b;
            }

            if (itemstack2.isEmpty()) {
                slot.set(ItemStack.b);
            }

            slot.d();
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.b;
            }

            this.e = true;
            slot.a(entityhuman, itemstack2);
            this.e = false;
            this.c();
        }

        return itemstack;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.resultInventory.splitWithoutUpdate(2);
        this.containerAccess.a((world, blockposition) -> {
            this.a(entityhuman, entityhuman.world, this.inventory);
        });
    }
}
