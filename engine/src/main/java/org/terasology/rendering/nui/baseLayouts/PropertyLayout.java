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

import org.terasology.input.events.KeyEvent;
import org.terasology.input.events.MouseButtonEvent;
import org.terasology.input.events.MouseWheelEvent;
import org.terasology.math.Border;
import org.terasology.math.Rect2i;
import org.terasology.math.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreLayout;
import org.terasology.rendering.nui.LayoutHint;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.baseWidgets.UILabel;
import org.terasology.rendering.nui.properties.Property;
import org.terasology.rendering.nui.properties.PropertyProvider;
import org.terasology.utilities.collection.NullIterator;

import java.util.Iterator;

/**
 * Created by synopia on 03.01.14.
 */
public class PropertyLayout extends CoreLayout<LayoutHint> {
    private Border padding = new Border(0, 0, 0, 0);

    private Vector2i labelSize = new Vector2i(100,20);
    private PropertyProvider<?> propertyProvider;

    public PropertyLayout() {
    }

    public PropertyLayout(String id) {
        super(id);
    }

    public Border getPadding() {
        return padding;
    }

    public void setPadding(Border padding) {
        this.padding = padding;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (propertyProvider!=null && !propertyProvider.isEmpty()) {
            Vector2i size = canvas.size();
            findLabelSize(canvas, size);
            Vector2i currentOffset = new Vector2i();
            int heightRemaining = size.y;
            for (Property<?, ?> property : propertyProvider.getProperties()) {
                UILabel label = property.getLabel();
                Rect2i drawRegion = Rect2i.createFromMinAndSize(currentOffset.x, currentOffset.y, labelSize.x, labelSize.y);
                canvas.drawElement(label, drawRegion);
                currentOffset.x += labelSize.x;

                UIWidget editor = property.getEditor();
                int editorWidth = size.x - labelSize.x;

                Vector2i editorSize = canvas.calculateSize(editor, new Vector2i(editorWidth, heightRemaining));
                drawRegion = Rect2i.createFromMinAndSize(currentOffset.x, currentOffset.y, editorSize.x, editorSize.y);
                canvas.drawElement(editor, drawRegion);
                currentOffset.x = 0;
                currentOffset.y += editorSize.y;
                heightRemaining -= editorSize.y;
            }
        }
    }

    private void findLabelSize(Canvas canvas, Vector2i areaHint) {
        labelSize = new Vector2i();
        for (Property<?, ?> property : propertyProvider.getProperties()) {
            Vector2i size = canvas.calculateSize(property.getLabel(), areaHint);
            if( size.x> labelSize.x) {
                labelSize.x = size.x;
            }
            if( size.y> labelSize.y) {
                labelSize.y = size.y;
            }
        }
    }

    @Override
    public void update(float delta) {
        for (Property<?, ?> property : propertyProvider.getProperties()) {
            property.getEditor().update(delta);
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent event) {
    }

    @Override
    public void onMouseWheelEvent(MouseWheelEvent event) {
    }

    @Override
    public void onKeyEvent(KeyEvent event) {
    }

    @Override
    public Iterator<UIWidget> iterator() {
        if( propertyProvider!=null ) {
            return new Iterator<UIWidget>() {
                private Iterator<Property<?,?>> propertyIterator = propertyProvider.getProperties().iterator();

                @Override
                public boolean hasNext() {
                    return propertyIterator.hasNext();
                }

                @Override
                public UIWidget next() {
                    return propertyIterator.next().getEditor();
                }

                @Override
                public void remove() {
                    throw new IllegalStateException("Not supported!");
                }
            };
        } else {
            return NullIterator.newInstance();
        }
    }

    @Override
    public void addWidget(UIWidget element, LayoutHint hint) {

    }

    public void setPropertyProvider(PropertyProvider<?> propertyProvider) {
        this.propertyProvider = propertyProvider;
    }
}
