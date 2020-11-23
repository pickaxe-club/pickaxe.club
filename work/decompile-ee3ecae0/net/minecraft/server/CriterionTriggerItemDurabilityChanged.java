package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerItemDurabilityChanged extends CriterionTriggerAbstract<CriterionTriggerItemDurabilityChanged.a> {

    private static final MinecraftKey a = new MinecraftKey("item_durability_changed");

    public CriterionTriggerItemDurabilityChanged() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerItemDurabilityChanged.a;
    }

    @Override
    public CriterionTriggerItemDurabilityChanged.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("durability"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject.get("delta"));

        return new CriterionTriggerItemDurabilityChanged.a(criterionconditionitem, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggeritemdurabilitychanged_a) -> {
            return criteriontriggeritemdurabilitychanged_a.a(itemstack, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;
        private final CriterionConditionValue.IntegerRange b;
        private final CriterionConditionValue.IntegerRange c;

        public a(CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1) {
            super(CriterionTriggerItemDurabilityChanged.a);
            this.a = criterionconditionitem;
            this.b = criterionconditionvalue_integerrange;
            this.c = criterionconditionvalue_integerrange1;
        }

        public static CriterionTriggerItemDurabilityChanged.a a(CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerItemDurabilityChanged.a(criterionconditionitem, criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange.e);
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.a.a(itemstack) ? false : (!this.b.d(itemstack.h() - i) ? false : this.c.d(itemstack.getDamage() - i));
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("item", this.a.a());
            jsonobject.add("durability", this.b.d());
            jsonobject.add("delta", this.c.d());
            return jsonobject;
        }
    }
}
