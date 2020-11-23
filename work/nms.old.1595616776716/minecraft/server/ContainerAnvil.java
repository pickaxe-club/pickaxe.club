package net.minecraft.server;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
// CraftBukkit end

public class ContainerAnvil extends Container {

    private static final Logger LOGGER = LogManager.getLogger();
    private final IInventory resultInventory;
    private final IInventory repairInventory;
    public final ContainerProperty levelCost;
    private final ContainerAccess containerAccess;
    private int h;
    public String renameText;
    private final EntityHuman player;
    // CraftBukkit start
    public int maximumRepairCost = 40;
    private int lastLevelCost;
    private CraftInventoryView bukkitEntity;
    private PlayerInventory playerInventory;
    // CraftBukkit end

    public ContainerAnvil(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.a);
    }

    public ContainerAnvil(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.ANVIL, i);
        this.playerInventory = playerinventory; // CraftBukkit
        this.resultInventory = new InventoryCraftResult();
        this.repairInventory = new InventorySubcontainer(2) {
            @Override
            public void update() {
                super.update();
                ContainerAnvil.this.a((IInventory) this);
            }
        };
        this.levelCost = ContainerProperty.a();
        this.containerAccess = containeraccess;
        this.player = playerinventory.player;
        this.a(this.levelCost);
        this.a(new Slot(this.repairInventory, 0, 27, 47));
        this.a(new Slot(this.repairInventory, 1, 76, 47));
        this.a(new Slot(this.resultInventory, 2, 134, 47) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public boolean isAllowed(EntityHuman entityhuman) {
                return (entityhuman.abilities.canInstantlyBuild || entityhuman.expLevel >= ContainerAnvil.this.levelCost.get()) && ContainerAnvil.this.levelCost.get() > 0 && this.hasItem();
            }

            @Override
            public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
                if (!entityhuman.abilities.canInstantlyBuild) {
                    entityhuman.levelDown(-ContainerAnvil.this.levelCost.get());
                }

                ContainerAnvil.this.repairInventory.setItem(0, ItemStack.a);
                if (ContainerAnvil.this.h > 0) {
                    ItemStack itemstack1 = ContainerAnvil.this.repairInventory.getItem(1);

                    if (!itemstack1.isEmpty() && itemstack1.getCount() > ContainerAnvil.this.h) {
                        itemstack1.subtract(ContainerAnvil.this.h);
                        ContainerAnvil.this.repairInventory.setItem(1, itemstack1);
                    } else {
                        ContainerAnvil.this.repairInventory.setItem(1, ItemStack.a);
                    }
                } else {
                    ContainerAnvil.this.repairInventory.setItem(1, ItemStack.a);
                }

                ContainerAnvil.this.levelCost.set(0);
                containeraccess.a((world, blockposition) -> {
                    IBlockData iblockdata = world.getType(blockposition);

                    if (!entityhuman.abilities.canInstantlyBuild && iblockdata.a(TagsBlock.ANVIL) && entityhuman.getRandom().nextFloat() < 0.12F) {
                        IBlockData iblockdata1 = BlockAnvil.e(iblockdata);

                        if (iblockdata1 == null) {
                            world.a(blockposition, false);
                            world.triggerEffect(1029, blockposition, 0);
                        } else {
                            world.setTypeAndData(blockposition, iblockdata1, 2);
                            world.triggerEffect(1030, blockposition, 0);
                        }
                    } else {
                        world.triggerEffect(1030, blockposition, 0);
                    }

                });
                return itemstack;
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
    public void a(IInventory iinventory) {
        super.a(iinventory);
        if (iinventory == this.repairInventory) {
            this.e();
        }

    }

    public void e() {
        ItemStack itemstack = this.repairInventory.getItem(0);

        this.levelCost.set(1);
        int i = 0;
        byte b0 = 0;
        byte b1 = 0;

        if (itemstack.isEmpty()) {
            org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a); // CraftBukkit
            this.levelCost.set(0);
        } else {
            ItemStack itemstack1 = itemstack.cloneItemStack();
            ItemStack itemstack2 = this.repairInventory.getItem(1);
            Map<Enchantment, Integer> map = EnchantmentManager.a(itemstack1);
            int j = b0 + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());

            this.h = 0;
            if (!itemstack2.isEmpty()) {
                boolean flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.e(itemstack2).isEmpty();
                int k;
                int l;
                int i1;

                if (itemstack1.e() && itemstack1.getItem().a(itemstack, itemstack2)) {
                    k = Math.min(itemstack1.getDamage(), itemstack1.h() / 4);
                    if (k <= 0) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a); // CraftBukkit
                        this.levelCost.set(0);
                        return;
                    }

                    for (i1 = 0; k > 0 && i1 < itemstack2.getCount(); ++i1) {
                        l = itemstack1.getDamage() - k;
                        itemstack1.setDamage(l);
                        ++i;
                        k = Math.min(itemstack1.getDamage(), itemstack1.h() / 4);
                    }

                    this.h = i1;
                } else {
                    if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.e())) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a); // CraftBukkit
                        this.levelCost.set(0);
                        return;
                    }

                    if (itemstack1.e() && !flag) {
                        k = itemstack.h() - itemstack.getDamage();
                        i1 = itemstack2.h() - itemstack2.getDamage();
                        l = i1 + itemstack1.h() * 12 / 100;
                        int j1 = k + l;
                        int k1 = itemstack1.h() - j1;

                        if (k1 < 0) {
                            k1 = 0;
                        }

                        if (k1 < itemstack1.getDamage()) {
                            itemstack1.setDamage(k1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1 = EnchantmentManager.a(itemstack2);
                    boolean flag1 = false;
                    boolean flag2 = false;
                    Iterator iterator = map1.keySet().iterator();

                    while (iterator.hasNext()) {
                        Enchantment enchantment = (Enchantment) iterator.next();

                        if (enchantment != null) {
                            int l1 = map.containsKey(enchantment) ? (Integer) map.get(enchantment) : 0;
                            int i2 = (Integer) map1.get(enchantment);

                            i2 = l1 == i2 ? i2 + 1 : Math.max(i2, l1);
                            boolean flag3 = enchantment.canEnchant(itemstack);

                            if (this.player.abilities.canInstantlyBuild || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                                flag3 = true;
                            }

                            Iterator iterator1 = map.keySet().iterator();

                            while (iterator1.hasNext()) {
                                Enchantment enchantment1 = (Enchantment) iterator1.next();

                                if (enchantment1 != enchantment && !enchantment.isCompatible(enchantment1)) {
                                    flag3 = false;
                                    ++i;
                                }
                            }

                            if (!flag3) {
                                flag2 = true;
                            } else {
                                flag1 = true;
                                if (i2 > enchantment.getMaxLevel()) {
                                    i2 = enchantment.getMaxLevel();
                                }

                                map.put(enchantment, i2);
                                int j2 = 0;

                                switch (enchantment.d()) {
                                    case COMMON:
                                        j2 = 1;
                                        break;
                                    case UNCOMMON:
                                        j2 = 2;
                                        break;
                                    case RARE:
                                        j2 = 4;
                                        break;
                                    case VERY_RARE:
                                        j2 = 8;
                                }

                                if (flag) {
                                    j2 = Math.max(1, j2 / 2);
                                }

                                i += j2 * i2;
                                if (itemstack.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }
                    }

                    if (flag2 && !flag1) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), ItemStack.a); // CraftBukkit
                        this.levelCost.set(0);
                        return;
                    }
                }
            }

            if (StringUtils.isBlank(this.renameText)) {
                if (itemstack.hasName()) {
                    b1 = 1;
                    i += b1;
                    itemstack1.s();
                }
            } else if (!this.renameText.equals(itemstack.getName().getString())) {
                b1 = 1;
                i += b1;
                itemstack1.a((IChatBaseComponent) (new ChatComponentText(this.renameText)));
            }

            this.levelCost.set(j + i);
            if (i <= 0) {
                itemstack1 = ItemStack.a;
            }

            if (b1 == i && b1 > 0 && this.levelCost.get() >= maximumRepairCost) { // CraftBukkit
                this.levelCost.set(maximumRepairCost - 1); // CraftBukkit
            }

            if (this.levelCost.get() >= maximumRepairCost && !this.player.abilities.canInstantlyBuild) { // CraftBukkit
                itemstack1 = ItemStack.a;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getRepairCost();

                if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
                    k2 = itemstack2.getRepairCost();
                }

                if (b1 != i || b1 == 0) {
                    k2 = d(k2);
                }

                itemstack1.setRepairCost(k2);
                EnchantmentManager.a(map, itemstack1);
            }

            org.bukkit.craftbukkit.event.CraftEventFactory.callPrepareAnvilEvent(getBukkitView(), itemstack1); // CraftBukkit
            this.c();
        }
    }

    public static int d(int i) {
        return i * 2 + 1;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.containerAccess.a((world, blockposition) -> {
            this.a(entityhuman, world, this.repairInventory);
        });
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return (Boolean) this.containerAccess.a((world, blockposition) -> {
            return !world.getType(blockposition).a(TagsBlock.ANVIL) ? false : entityhuman.g((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return ItemStack.a;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 39 && !this.a(itemstack1, 0, 2, false)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
                return ItemStack.a;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.a);
            } else {
                slot.d();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.a;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    public void a(String s) {
        this.renameText = s;
        if (this.getSlot(2).hasItem()) {
            ItemStack itemstack = this.getSlot(2).getItem();

            if (StringUtils.isBlank(s)) {
                itemstack.s();
            } else {
                itemstack.a((IChatBaseComponent) (new ChatComponentText(this.renameText)));
            }
        }

        this.e();
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        org.bukkit.craftbukkit.inventory.CraftInventory inventory = new org.bukkit.craftbukkit.inventory.CraftInventoryAnvil(
                containerAccess.getLocation(), this.repairInventory, this.resultInventory, this);
        bukkitEntity = new CraftInventoryView(this.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
