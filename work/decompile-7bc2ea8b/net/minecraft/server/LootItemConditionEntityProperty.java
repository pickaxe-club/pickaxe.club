package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;

public class LootItemConditionEntityProperty implements LootItemCondition {

    private final CriterionConditionEntity a;
    private final LootTableInfo.EntityTarget b;

    private LootItemConditionEntityProperty(CriterionConditionEntity criterionconditionentity, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        this.a = criterionconditionentity;
        this.b = loottableinfo_entitytarget;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.e;
    }

    @Override
    public Set<LootContextParameter<?>> a() {
        return ImmutableSet.of(LootContextParameters.POSITION, this.b.a());
    }

    public boolean test(LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getContextParameter(this.b.a());
        Vec3D vec3d = (Vec3D) loottableinfo.getContextParameter(LootContextParameters.g);

        return this.a.a(loottableinfo.getWorld(), vec3d, entity);
    }

    public static LootItemCondition.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return a(loottableinfo_entitytarget, CriterionConditionEntity.a.a());
    }

    public static LootItemCondition.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget, CriterionConditionEntity.a criterionconditionentity_a) {
        return () -> {
            return new LootItemConditionEntityProperty(criterionconditionentity_a.b(), loottableinfo_entitytarget);
        };
    }

    public static LootItemCondition.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget, CriterionConditionEntity criterionconditionentity) {
        return () -> {
            return new LootItemConditionEntityProperty(criterionconditionentity, loottableinfo_entitytarget);
        };
    }

    public static class a implements LootSerializer<LootItemConditionEntityProperty> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionEntityProperty lootitemconditionentityproperty, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("predicate", lootitemconditionentityproperty.a.a());
            jsonobject.add("entity", jsonserializationcontext.serialize(lootitemconditionentityproperty.b));
        }

        @Override
        public LootItemConditionEntityProperty a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("predicate"));

            return new LootItemConditionEntityProperty(criterionconditionentity, (LootTableInfo.EntityTarget) ChatDeserializer.a(jsonobject, "entity", jsondeserializationcontext, LootTableInfo.EntityTarget.class));
        }
    }
}
