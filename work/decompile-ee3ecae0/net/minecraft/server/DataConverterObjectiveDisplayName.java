package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;

public class DataConverterObjectiveDisplayName extends DataFix {

    public DataConverterObjectiveDisplayName(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, Dynamic<?>>> type = DSL.named(DataConverterTypes.OBJECTIVE.typeName(), DSL.remainderType());

        if (!Objects.equals(type, this.getInputSchema().getType(DataConverterTypes.OBJECTIVE))) {
            throw new IllegalStateException("Objective type is not what was expected.");
        } else {
            return this.fixTypeEverywhere("ObjectiveDisplayNameFix", type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond((dynamic) -> {
                        return dynamic.update("DisplayName", (dynamic1) -> {
                            Optional optional = dynamic1.asString().map((s) -> {
                                return IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) (new ChatComponentText(s)));
                            });

                            dynamic.getClass();
                            return (Dynamic) DataFixUtils.orElse(optional.map(dynamic::createString), dynamic1);
                        });
                    });
                };
            });
        }
    }
}
