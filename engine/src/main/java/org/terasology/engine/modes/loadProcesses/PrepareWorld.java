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

package org.terasology.engine.modes.loadProcesses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.registry.CoreRegistry;
import org.terasology.engine.EngineTime;
import org.terasology.engine.Time;
import org.terasology.engine.modes.LoadProcess;
import org.terasology.rendering.world.WorldRenderer;

/**
 * @author Immortius
 */
public class PrepareWorld implements LoadProcess {
    private static final Logger logger = LoggerFactory.getLogger(PrepareWorld.class);

    private long startTime;
    private WorldRenderer worldRenderer;

    @Override
    public String getMessage() {
        return "Caching World...";
    }

    @Override
    public boolean step() {
        if (worldRenderer.pregenerateChunks()) {
            return true;
        }
        EngineTime time = (EngineTime) CoreRegistry.get(Time.class);
        long totalTime = time.getRawTimeInMs() - startTime;
        return totalTime > 5000;
    }

    @Override
    public void begin() {
        worldRenderer = CoreRegistry.get(WorldRenderer.class);
        EngineTime time = (EngineTime) CoreRegistry.get(Time.class);
        startTime = time.getRawTimeInMs();
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public int getExpectedCost() {
        return 5;
    }

}
