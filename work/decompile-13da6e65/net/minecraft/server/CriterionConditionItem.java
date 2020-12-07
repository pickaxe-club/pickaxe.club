package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class CriterionConditionItem {

    public static final CriterionConditionItem a = new CriterionConditionItem();
    @Nullable
    private final Tag<Item> b;
    @Nullable
    private final Item c;
    private final CriterionConditionValue.IntegerRange d;
    private final CriterionConditionValue.IntegerRange e;
    private final CriterionConditionEnchantments[] f;
    private final CriterionConditionEnchantments[] g;
    @Nullable
    private final PotionRegistry h;
    private final CriterionConditionNBT i;

    public CriterionConditionItem() {
        this.b = null;
        this.c = null;
        this.h = null;
        this.d = CriterionConditionValue.IntegerRange.e;
        this.e = CriterionConditionValue.IntegerRange.e;
        this.f = CriterionConditionEnchantments.b;
        this.g = CriterionConditionEnchantments.b;
        this.i = CriterionConditionNBT.a;
    }

    public CriterionConditionItem(@Nullable Tag<Item> tag, @Nullable Item item, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1, CriterionConditionEnchantments[] acriterionconditionenchantments, CriterionConditionEnchantments[] acriterionconditionenchantments1, @Nullable PotionRegistry potionregistry, CriterionConditionNBT criterionconditionnbt) {
        this.b = tag;
        this.c = item;
        this.d = criterionconditionvalue_integerrange;
        this.e = criterionconditionvalue_integerrange1;
        this.f = acriterionconditionenchantments;
        this.g = acriterionconditionenchantments1;
        this.h = potionregistry;
        this.i = criterionconditionnbt;
    }

    public boolean a(ItemStack itemstack) {
        if (this == CriterionConditionItem.a) {
            return true;
        } else if (this.b != null && !this.b.isTagged(itemstack.getItem())) {
            return false;
        } else if (this.c != null && itemstack.getItem() != this.c) {
            return false;
        } else if (!this.d.d(itemstack.getCount())) {
            return false;
        } else if (!this.e.c() && !itemstack.e()) {
            return false;
        } else if (!this.e.d(itemstack.h() - itemstack.getDamage())) {
            return false;
        } else if (!this.i.a(itemstack)) {
            return false;
        } else {
            Map map;
            CriterionConditionEnchantments[] acriterionconditionenchantments;
            int i;
            CriterionConditionEnchantments criterionconditionenchantments;
            int j;

            if (this.f.length > 0) {
                map = EnchantmentManager.a(itemstack.getEnchantments());
                acriterionconditionenchantments = this.f;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    if (!criterionconditionenchantments.a(map)) {
                        return false;
                    }
                }
            }

            if (this.g.length > 0) {
                map = EnchantmentManager.a(ItemEnchantedBook.d(itemstack));
                acriterionconditionenchantments = this.g;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    if (!criterionconditionenchantments.a(map)) {
                        return false;
                    }
                }
            }

            PotionRegistry potionregistry = PotionUtil.d(itemstack);

            return this.h == null || this.h == potionregistry;
        }
    }

    public static CriterionConditionItem a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "item");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("count"));
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject.get("durability"));

            if (jsonobject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));
                Item item = null;

                if (jsonobject.has("item")) {
                    MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "item"));

                    item = (Item) IRegistry.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown item id '" + minecraftkey + "'");
                    });
                }

                Tag<Item> tag = null;

                if (jsonobject.has("tag")) {
                    MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));

                    tag = TagsInstance.a().getItemTags().a(minecraftkey1);
                    if (tag == null) {
                        throw new JsonSyntaxException("Unknown item tag '" + minecraftkey1 + "'");
                    }
                }

                PotionRegistry potionregistry = null;

                if (jsonobject.has("potion")) {
                    MinecraftKey minecraftkey2 = new MinecraftKey(ChatDeserializer.h(jsonobject, "potion"));

                    potionregistry = (PotionRegistry) IRegistry.POTION.getOptional(minecraftkey2).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown potion '" + minecraftkey2 + "'");
                    });
                }

                CriterionConditionEnchantments[] acriterionconditionenchantments = CriterionConditionEnchantments.b(jsonobject.get("enchantments"));
                CriterionConditionEnchantments[] acriterionconditionenchantments1 = CriterionConditionEnchantments.b(jsonobject.get("stored_enchantments"));

                return new CriterionConditionItem(tag, item, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1, acriterionconditionenchantments, acriterionconditionenchantments1, potionregistry, criterionconditionnbt);
            }
        } else {
            return CriterionConditionItem.a;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionItem.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.c != null) {
                jsonobject.addProperty("item", IRegistry.ITEM.getKey(this.c).toString());
            }

            if (this.b != null) {
                jsonobject.addProperty("tag", TagsInstance.a().getItemTags().b(this.b).toString());
            }

            jsonobject.add("count", this.d.d());
            jsonobject.add("durability", this.e.d());
            jsonobject.add("nbt", this.i.a());
            JsonArray jsonarray;
            CriterionConditionEnchantments[] acriterionconditionenchantments;
            int i;
            CriterionConditionEnchantments criterionconditionenchantments;
            int j;

            if (this.f.length > 0) {
                jsonarray = new JsonArray();
                acriterionconditionenchantments = this.f;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    jsonarray.add(criterionconditionenchantments.a());
                }

                jsonobject.add("enchantments", jsonarray);
            }

            if (this.g.length > 0) {
                jsonarray = new JsonArray();
                acriterionconditionenchantments = this.g;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    jsonarray.add(criterionconditionenchantments.a());
                }

                jsonobject.add("stored_enchantments", jsonarray);
            }

            if (this.h != null) {
                jsonobject.addProperty("potion", IRegistry.POTION.getKey(this.h).toString());
            }

            return jsonobject;
        }
    }

    public static CriterionConditionItem[] b(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.n(jsonelement, "items");
            CriterionConditionItem[] acriterionconditionitem = new CriterionConditionItem[jsonarray.size()];

            for (int i = 0; i < acriterionconditionitem.length; ++i) {
                acriterionconditionitem[i] = a(jsonarray.get(i));
            }

            return acriterionconditionitem;
        } else {
            return new CriterionConditionItem[0];
        }
    }

    public static class a {

        private final List<CriterionConditionEnchantments> a = Lists.newArrayList();
        private final List<CriterionConditionEnchantments> b = Lists.newArrayList();
        @Nullable
        private Item c;
        @Nullable
        private Tag<Item> d;
        private CriterionConditionValue.IntegerRange e;
        private CriterionConditionValue.IntegerRange f;
        @Nullable
        private PotionRegistry g;
        private CriterionConditionNBT h;

        private a() {
            this.e = CriterionConditionValue.IntegerRange.e;
            this.f = CriterionConditionValue.IntegerRange.e;
            this.h = CriterionConditionNBT.a;
        }

        public static CriterionConditionItem.a a() {
            return new CriterionConditionItem.a();
        }

        public CriterionConditionItem.a a(IMaterial imaterial) {
            this.c = imaterial.getItem();
            return this;
        }

        public CriterionConditionItem.a a(Tag<Item> tag) {
            this.d = tag;
            return this;
        }

        public CriterionConditionItem.a a(NBTTagCompound nbttagcompound) {
            this.h = new CriterionConditionNBT(nbttagcompound);
            return this;
        }

        public CriterionConditionItem.a a(CriterionConditionEnchantments criterionconditionenchantments) {
            this.a.add(criterionconditionenchantments);
            return this;
        }

        public CriterionConditionItem b() {
            return new CriterionConditionItem(this.d, this.c, this.e, this.f, (CriterionConditionEnchantments[]) this.a.toArray(CriterionConditionEnchantments.b), (CriterionConditionEnchantments[]) this.b.toArray(CriterionConditionEnchantments.b), this.g, this.h);
        }
    }
}
