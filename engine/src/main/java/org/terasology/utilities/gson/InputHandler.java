/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.utilities.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.terasology.input.Input;
import org.terasology.input.InputType;

import java.lang.reflect.Type;

/**
 * @author Immortius
 */
public class InputHandler implements JsonSerializer<Input>, JsonDeserializer<Input> {

    @Override
    public Input deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return InputType.parse(json.getAsString().replace(" ", "_"));
    }

    @Override
    public JsonElement serialize(Input src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
