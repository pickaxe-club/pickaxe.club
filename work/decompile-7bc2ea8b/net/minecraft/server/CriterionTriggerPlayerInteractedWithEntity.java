package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerPlayerInteractedWithEntity extends CriterionTriggerAbstract<CriterionTriggerPlayerInteractedWithEntity.a> {

    private static final MinecraftKey a = new MinecraftKey("player_interacted_with_entity");

    public CriterionTriggerPlayerInteractedWithEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerPlayerInteractedWithEntity.a;
    }

    @Override
    protected CriterionTriggerPlayerInteractedWithEntity.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerPlayerInteractedWithEntity.a(criterionconditionentity_b, criterionconditionitem, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerplayerinteractedwithentity_a) -> {
            return criteriontriggerplayerinteractedwithentity_a.a(itemstack, loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;
        private final CriterionConditionEntity.b b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem criterionconditionitem, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerPlayerInteractedWithEntity.a, criterionconditionentity_b);
            this.a = criterionconditionitem;
            this.b = criterionconditionentity_b1;
        }

        public static CriterionTriggerPlayerInteractedWithEntity.a a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionItem.a criterionconditionitem_a, CriterionConditionEntity.b criterionconditionentity_b1) {
            return new CriterionTriggerPlayerInteractedWithEntity.a(criterionconditionentity_b, criterionconditionitem_a.b(), criterionconditionentity_b1);
        }

        public boolean a(ItemStack itemstack, LootTableInfo loottableinfo) {
            return !this.a.a(itemstack) ? false : this.b.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("item", this.a.a());
            jsonobject.add("entity", this.b.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
