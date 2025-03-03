package eu.pb4.polymer.core.mixin.item.component;

import eu.pb4.polymer.core.api.utils.PolymerObject;
import eu.pb4.polymer.core.impl.TransformingDataComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(FoodComponent.class)
public abstract class FoodComponentMixin implements TransformingDataComponent {
    @Shadow @Final private int nutrition;

    @Shadow @Final private boolean canAlwaysEat;

    @Shadow @Final private float eatSeconds;

    @Shadow @Final private List<FoodComponent.StatusEffectEntry> effects;

    @Shadow @Final private float saturation;

    @Override
    public Object polymer$getTransformed(ServerPlayerEntity player) {
        if (!polymer$requireModification(player)) {
            return this;
        }

        return new FoodComponent(this.nutrition, this.saturation, this.canAlwaysEat, this.eatSeconds, List.of());
    }

    @Override
    public boolean polymer$requireModification(ServerPlayerEntity player) {
        for (var effect : this.effects) {
            if (effect.effect().getEffectType().value() instanceof PolymerObject) {
                return true;
            }
        }
        return false;
    }
}
