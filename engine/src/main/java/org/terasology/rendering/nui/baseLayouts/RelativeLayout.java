/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.baseLayouts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Rect2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreLayout;
import org.terasology.rendering.nui.HorizontalAlign;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.VerticalAlign;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Immortius
 */
public class RelativeLayout extends CoreLayout<RelativeLayoutHint> {

    private static final Logger logger = LoggerFactory.getLogger(RelativeLayout.class);

    private Map<String, WidgetInfo> contentLookup = Maps.newHashMap();
    private List<WidgetInfo> contents = Lists.newArrayList();

    private Map<WidgetInfo, Rect2i> cachedRegions = Maps.newHashMap();

    private String loopDetectionId = "";

    @Override
    public void addWidget(UIWidget element, RelativeLayoutHint hint) {
        WidgetInfo info = new WidgetInfo(element, hint);
        contents.add(info);
        if (!element.getId().isEmpty()) {
            contentLookup.put(element.getId(), info);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (WidgetInfo element : contents) {
            Rect2i drawRegion = getRegion(element, canvas);
            canvas.drawElement(element.widget, drawRegion);
        }
        cachedRegions.clear();
    }

    private Rect2i getRegion(WidgetInfo element, Canvas canvas) {
        Rect2i cachedRegion = cachedRegions.get(element);
        if (cachedRegion != null) {
            return cachedRegion;
        }

        int left = 0;
        int right = canvas.size().x;
        int center = canvas.size().x / 2;
        if (element.layoutHint.getPositionCenterHorizontal() != null) {
            RelativeLayoutHint.HorizontalInfo info = element.layoutHint.getPositionCenterHorizontal();
            Rect2i targetRegion = getTargetRegion(info.getWidget(), canvas);
            HorizontalAlign align = (info.getTarget() != null) ? info.getTarget() : HorizontalAlign.CENTER;
            center = align.getStart(targetRegion) + info.getOffset();
        }
        if (element.layoutHint.getPositionLeft() != null) {
            RelativeLayoutHint.HorizontalInfo info = element.layoutHint.getPositionLeft();
            Rect2i targetRegion = getTargetRegion(info.getWidget(), canvas);
            HorizontalAlign align = (info.getTarget() != null) ? info.getTarget() : HorizontalAlign.LEFT;
            left = align.getStart(targetRegion) + info.getOffset();
        }
        if (element.layoutHint.getPositionRight() != null) {
            RelativeLayoutHint.HorizontalInfo info = element.layoutHint.getPositionLeft();
            Rect2i targetRegion = getTargetRegion(info.getWidget(), canvas);
            HorizontalAlign align = (info.getTarget() != null) ? info.getTarget() : HorizontalAlign.RIGHT;
            right = align.getStart(targetRegion) - info.getOffset();
        }

        int width = element.layoutHint.getWidth();
        if (width == 0) {
            width = right - left;
        } else {
            if (element.layoutHint.getPositionCenterHorizontal() != null) {
                left = center - width / 2;
            } else if (element.layoutHint.getPositionRight() != null) {
                left = right - width;
            }
        }

        int top = 0;
        int bottom = canvas.size().y;
        int vcenter = canvas.size().y / 2;
        if (element.layoutHint.getPositionCenterVertical() != null) {
            RelativeLayoutHint.VerticalInfo info = element.layoutHint.getPositionCenterVertical();
            Rect2i targetRegion = getTargetRegion(info.getWidget(), canvas);
            VerticalAlign align = (info.getTarget() != null) ? info.getTarget() : VerticalAlign.MIDDLE;
            vcenter = align.getStart(targetRegion) + info.getOffset();
        }
        if (element.layoutHint.getPositionTop() != null) {
            RelativeLayoutHint.VerticalInfo info = element.layoutHint.getPositionTop();
            Rect2i targetRegion = getTargetRegion(info.getWidget(), canvas);
            VerticalAlign align = (info.getTarget() != null) ? info.getTarget() : VerticalAlign.TOP;
            top = align.getStart(targetRegion) + info.getOffset();
        }
        if (element.layoutHint.getPositionBottom() != null) {
            RelativeLayoutHint.VerticalInfo info = element.layoutHint.getPositionBottom();
            Rect2i targetRegion = getTargetRegion(info.getWidget(), canvas);
            VerticalAlign align = (info.getTarget() != null) ? info.getTarget() : VerticalAlign.BOTTOM;
            bottom = align.getStart(targetRegion) - info.getOffset();
        }

        int height = element.layoutHint.getHeight();
        if (height == 0) {
            height = bottom - top;
        } else {
            if (element.layoutHint.getPositionCenterVertical() != null) {
                top = vcenter - height / 2;
            } else if (element.layoutHint.getPositionBottom() != null) {
                top = bottom - height;
            }
        }
        Rect2i region = Rect2i.createFromMinAndSize(left, top, width, height);
        cachedRegions.put(element, region);
        return region;
    }

    private Rect2i getTargetRegion(String id, Canvas canvas) {
        if (id != null && !id.isEmpty()) {
            if (loopDetectionId.equals(id)) {
                logger.warn("Infinite loop detected resolving layout of element {}", loopDetectionId);
                loopDetectionId = "";
                return canvas.getRegion();
            } else if (loopDetectionId.isEmpty()) {
                loopDetectionId = id;
            }
            WidgetInfo target = contentLookup.get(id);
            if (target != null) {
                return getRegion(target, canvas);
            }
        }
        loopDetectionId = "";
        return canvas.getRegion();
    }

    @Override
    public Iterator<UIWidget> iterator() {
        List<UIWidget> widgets = Lists.newArrayListWithCapacity(contents.size());
        for (WidgetInfo info : contents) {
            widgets.add(info.widget);
        }
        return widgets.iterator();
    }

    private static final class WidgetInfo {
        private UIWidget widget;
        private RelativeLayoutHint layoutHint;

        private WidgetInfo(UIWidget widget, RelativeLayoutHint layoutHint) {
            this.widget = widget;
            this.layoutHint = layoutHint;
        }
    }
}