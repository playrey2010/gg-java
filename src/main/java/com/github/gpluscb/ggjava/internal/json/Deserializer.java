package com.github.gpluscb.ggjava.internal.json;

import com.github.gpluscb.ggjava.entity.EntityType;
import com.github.gpluscb.ggjava.entity.object.response.GGResponseObject;
import com.github.gpluscb.ggjava.entity.object.response.ListResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class Deserializer {
	@Nullable
	public static <T extends GGResponseObject> T deserialize(@Nonnull JsonElement json, @Nonnull Class<T> toClass) {
		if(json.isJsonNull()) {
			return null;
		} else if(json.isJsonObject()) {
			return deserialize(json.getAsJsonObject(), toClass);
		} else if(json.isJsonArray()) {
			throw new IllegalStateException("For deserializing JsonArrays, use <T extends GGResponseObject> ListResponse<T> deserialize(JsonArray, Class<T> innerClass)");
		} else if(json.isJsonPrimitive()) {
			return deserialize(json.getAsJsonPrimitive(), toClass);
		}

		throw new IllegalStateException("Illegal state reached: json is neither JsonNull, JsonObject, JsonArray nor JsonPrimitive");
	}

	@Nonnull
	public static <T extends GGResponseObject> T deserialize(@Nonnull JsonObject json, @Nonnull Class<T> toClass) {
		// Getting constructors
		Constructor<T> constructor = null;
		for(Constructor<?> c : toClass.getConstructors()) {
			if(c.getParameterCount() != 0) {
				// See documentation of getConstructors, this can only be Constructor<T>
				constructor = (Constructor<T>) c;
				break;
			}
		}

		// TODO: DeserializationException
		if(constructor == null) throw new IllegalStateException("No fitting constructor found for " + toClass.toString());

		Object[] constructorArgs = new Object[constructor.getParameterCount()];

		Parameter[] params = constructor.getParameters();
		for(int i = 0; i < params.length; i++) {
			Parameter param = params[i];

			String name = param.getName();
			Class<?> paramType = param.getType();

			if(json.has(name)) {
				JsonElement value = json.get(name);

				if(!GGResponseObject.class.isAssignableFrom(paramType))
					throw new IllegalStateException("Type of parameter " + i + " of the constructor " + toClass.toString() + " ( type is " + paramType.toString() + ") does mot extend GGResponseObject");

				if(value.isJsonArray()) {
					Type paramParameterizedType = param.getParameterizedType();

					if(!(paramParameterizedType instanceof ParameterizedType)) throw new IllegalStateException("Cannot retrieve parameterized types for parameter " + param.toString());

					Type[] typeArguments = ((ParameterizedType) paramParameterizedType).getActualTypeArguments();

					if(typeArguments.length != 1) throw new IllegalStateException("Unexpected number of generic type parameters for param " + param.toString());

					if(!(typeArguments[0] instanceof Class)) throw new IllegalStateException("Generic type parameter does not implement class");

					// No way to check if GGResponseObject is safe
					Class<? extends GGResponseObject> classArgument = (Class<? extends GGResponseObject>) typeArguments[0];

					constructorArgs[i] = deserialize(value.getAsJsonArray(), classArgument);
				} else {
					constructorArgs[i] = deserialize(value, (Class<? extends GGResponseObject>) paramType);
				}
			} else {
				// Not provided, invoke no args constructor for this case
				try {
					// Get no args constructor
					Constructor<?> paramConstructorNoArgs = paramType.getConstructor();

					Object arg = paramConstructorNoArgs.newInstance();

					constructorArgs[i] = arg;
				} catch (NoSuchMethodException e) {
					throw new IllegalStateException("No fitting constructor found for " + paramType.toString(), e);
				} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
					throw new IllegalStateException("Exception caught while invoking no args constructor for " + paramType.toString(), e);
				}
			}
		}

		try {
			return constructor.newInstance(constructorArgs);
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
			throw new IllegalStateException("Exception caught while invoking constructor for " + toClass.toString(), e);
		}
	}

	@Nonnull
	public static <T extends GGResponseObject> ListResponse<T> deserialize(@Nonnull JsonArray json, @Nonnull Class<T> toClass) {
		EntityType type = EntityType.getByClazz(toClass);
		if(type == null) throw new IllegalStateException("No EntityType found for class " + toClass.toString());

		List<T> ret = new ArrayList<>();

		for(JsonElement element : json)
			ret.add(deserialize(element, toClass));

		return new ListResponse<>(type, ret);
	}

	@Nonnull
	public static <T extends GGResponseObject> T deserialize(@Nonnull JsonPrimitive json, @Nonnull Class<T> toClass) {
		// TODO
		return null;
	}
}
