package taskmanager.adapters;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapter<T> implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    private final Type typeOfT;

    public OptionalTypeAdapter(Type typeOfT) {
        this.typeOfT = typeOfT;
    }

    @Override
    public JsonElement serialize(Optional<T> optional, Type typeOfSrc, JsonSerializationContext context) {
        return optional.isPresent() ? context.serialize(optional.get(), typeOfT) : JsonNull.INSTANCE;
    }

    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.isJsonNull() ? Optional.empty() : Optional.ofNullable(context.deserialize(json, typeOfT));
    }
}

