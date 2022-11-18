/*
 * $Id$
 *
 * Copyright (c) 2022 Janusch Rentenatus
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'JaRe' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.simsilica.lemur;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.GuiComponent;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.state.FilterState;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.text.SelDocumentModel;
import de.jare.textfilter.FSelection;
import de.jare.textfilter.TextFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Janusch Rentenatus
 */
public class PopUpListPicker extends PopUp {

    public static PopUpListPicker onButton(final Application app, Button popButton, List<?> items, final float scale) {
        final PopUpListPicker ret = new PopUpListPicker(items);
        popButton.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button arg0) {
                ret.show(app, (arg0.getWorldScale().x + arg0.getWorldScale().y) * 0.5f);
            }
        });
        return ret;
    }
    private final List<?> items;
    private final SelTextField filterTextfield;
    private final VersionedReference<SelDocumentModel> textfieldRef;
    private ListBox listBox;
    private final List<Command<PopUpListPicker>> pickCommands;
    private Object selectedItem;
    private Object pickedItem;
    private FilterState filterState;
    private TextFilter filter;
    private ColorRGBA foundColor1 = new ColorRGBA(0.01f, 0.42f, 0.52f, 1f);
    private ColorRGBA foundColor2 = new ColorRGBA(0.41f, 0.52f, 0.2f, 1f);
    private ColorRGBA foundColor3 = new ColorRGBA(0.3f, 0.1f, 0.4f, 1f);
    private ColorRGBA foundColor4 = new ColorRGBA(0.3f, 0.1f, 0.4f, 1f);

    private PopUpListPicker(List<?> items) {
        this.items = items;
        this.pickCommands = new ArrayList<>();
        this.filterTextfield = new SelTextField("");
        this.textfieldRef = filterTextfield.getDocumentModel().createReference();
        this.selectedItem = null;
        this.pickedItem = null;
        this.filterState = null;
        this.filter = null;
    }

    @Override
    protected void rebuild() {
        clearChildren();
        Insets3f insets3f = new Insets3f(
                getWorldScale().getY() + 1.4f,
                getWorldScale().getX(),
                getWorldScale().getY() + 1.2f,
                getWorldScale().getX());
        Container inner = addChild(new Container());
        //inner.setLayout(new BorderLayout());
        inner.setInsets(insets3f);

        inner.addChild(filterTextfield);
        listBox = new ListBox(new VersionedList<>(items));
        listBox.setVisibleItems(20);
        listBox.addClickCommands(new BoxClickCommand());
        listBox.setCellRenderer(new TextFilterRenderer());
        inner.addChild(listBox);

        filterTextfield.setPreferredSize(new Vector3f(360f, 18f, 0f));

        GuiComponent myBG = new QuadBackgroundComponent(new ColorRGBA(0.08f, 0.03f, 0.08f, 0.72f));
        inner.setBackground(myBG.clone());
        myBG = new QuadBackgroundComponent(new ColorRGBA(0.18f, 0.03f, 0.8f, 0.72f));
        setBackground(myBG.clone());
        readyUpdated();
    }

    public void checkFilter() {
        if (textfieldRef.update()) {
            listBox.getModel().removeIf(arg0 -> true);
            if (filterTextfield.getText().isEmpty()) {
                listBox.getModel().addAll(items);
                filter = null;
            } else {
                filter = new TextFilter(filterTextfield.getText());
                for (Object item : items) {
                    if (filter.check(String.valueOf(item))) {
                        listBox.getModel().add(item);
                    }
                }
            }
        }
    }

    private class TextFilterRenderer implements ValueRenderer {

        private ElementId elementId;
        private String style;

        public TextFilterRenderer() {
        }

        @Override
        public void configureStyle(ElementId elementId, String style) {
            if (this.elementId == null) {
                this.elementId = elementId;
            }
            if (this.style == null) {
                this.style = style;
            }
        }

        @Override
        public Panel getView(Object value, boolean selected, Panel existing) {
            SelTextField viewTextField = existing instanceof SelTextField
                    ? (SelTextField) existing : null;
            if (viewTextField == null) {
                viewTextField = new SelTextField(String.valueOf(value), elementId, style);
                viewTextField.setBackground(null);
                viewTextField.setreadonly(true);
                viewTextField.setFocusable(false);
                viewTextField.setSelectColor(foundColor1, foundColor2, foundColor3, foundColor4);
            } else {
                viewTextField.setText(String.valueOf(value));
            }
            viewTextField.getDocumentModel().emptyAnchors();
            if (filter != null) {
                for (FSelection fSel : filter.render(String.valueOf(value))) {
                    System.out.println(fSel.getStartpos() + ", " + fSel.getEndpos());
                    viewTextField.getDocumentModel().addTextselectArea(fSel.getStartpos(), fSel.getEndpos());
                }
            }

            viewTextField.setmaxLinecount(1);
            return viewTextField;
        }

    }

    protected class BoxClickCommand implements Command<ListBox> {

        long lastClick = 0;

        @Override
        public void execute(ListBox arg0) {
            long millis = System.currentTimeMillis();
            selectedItem = arg0.getSelectedItem();
            if (millis - lastClick < 450) {
                pickedItem = selectedItem;
                executePickCommands();
            }
            lastClick = millis;
        }
    }

    protected void executePickCommands() {
        for (Command<PopUpListPicker> comm : pickCommands) {
            comm.execute(this);
        }
    }

    public void addPickCommands(Command<PopUpListPicker>... comms) {
        for (Command<PopUpListPicker> comm : comms) {
            pickCommands.add(comm);
        }
    }

    public void removePickCommands(Command<PopUpListPicker>... comms) {
        for (Command<PopUpListPicker> comm : comms) {
            pickCommands.remove(comm);
        }
    }

    @Override
    public void close() {
        super.close();
        if (filterState != null) {
            filterState.getApplication().getStateManager().detach(filterState);
            filterState = null;
        }
    }

    @Override
    public void show(Application app, float scale) {
        super.show(app, scale);
        if (filterState == null) {
            filterState = new FilterState(app, this);
            app.getStateManager().attach(filterState);
        }
    }

    @Override
    public void show(Application app, ViewPort view, Panel panel, float inset) {
        super.show(app, view, panel, inset);
        if (filterState == null) {
            filterState = new FilterState(app, this);
            app.getStateManager().attach(filterState);
        }
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public Object getPickedItem() {
        return pickedItem;
    }

    public boolean isTextselect() {
        return filterTextfield.isTextselect();
    }

    public void setColor(ColorRGBA color) {
        filterTextfield.setColor(color);
    }

    public ColorRGBA getColor() {
        return filterTextfield.getColor();
    }

    public void setSelectColor(ColorRGBA newColor) {
        filterTextfield.setSelectColor(newColor);
    }

    public void setSelectColor(ColorRGBA newselectColor1, ColorRGBA newselectColor2, ColorRGBA newselectColor3, ColorRGBA newselectColor4) {
        filterTextfield.setSelectColor(newselectColor1, newselectColor2, newselectColor3, newselectColor4);
    }

    public ColorRGBA getSelectColor1() {
        return filterTextfield.getSelectColor1();
    }

    public ColorRGBA getSelectColor2() {
        return filterTextfield.getSelectColor2();
    }

    public ColorRGBA getSelectColor3() {
        return filterTextfield.getSelectColor3();
    }

    public ColorRGBA getSelectColor4() {
        return filterTextfield.getSelectColor4();
    }

    public void setFoundColor(ColorRGBA newColor) {
        foundColor1 = newColor;
        foundColor2 = newColor;
        foundColor3 = newColor;
        foundColor4 = newColor;
    }

    public void setFoundColor(ColorRGBA newfoundColor1, ColorRGBA newfoundColor2, ColorRGBA newfoundColor3, ColorRGBA newfoundColor4) {
        foundColor1 = newfoundColor1;
        foundColor2 = newfoundColor2;
        foundColor3 = newfoundColor3;
        foundColor4 = newfoundColor4;
    }

    public ColorRGBA getFoundColor1() {
        return foundColor1;
    }

    public ColorRGBA getFoundColor2() {
        return foundColor2;
    }

    public ColorRGBA getFoundColor3() {
        return foundColor3;
    }

    public ColorRGBA getFoundColor4() {
        return foundColor4;
    }
}
