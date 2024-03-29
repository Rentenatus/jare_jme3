/*
 * $Id$
 *
 * Copyright (c) 2012-2012 jMonkeyEngine
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
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
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

import java.util.Map;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.component.MenuItem;

import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SelTextEntryComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.FocusMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.event.SelKeyActionListener;
import com.simsilica.lemur.event.SelTextCommand;
import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleAttribute;
import com.simsilica.lemur.style.Styles;
import com.simsilica.lemur.text.DefaultSelDocumentModel;
import com.simsilica.lemur.text.SelDocumentModel;
import java.util.List;

/**
 * A GUI element allowing text entry.
 *
 * @author Paul Speed
 * @author Aufricer
 * @author Janusch Rentenatus
 */
public class SelTextField extends Panel {
    
    public static final String ELEMENT_ID = "textField";
    public static final String LAYER_TEXT = "text";
    
    private VersionedList<MenuItem> menuList = new VersionedList<>();
    
    public List<MenuItem> getMenuList() {
        return menuList;
    }
    
    public enum ScrollMode {
        None, Full, AutoadjustX, AutoadjustY
    }
    
    public enum TextselectMode {
        Off, Auto, Manuell
    }
    
    private SelTextEntryComponent text;
    
    public SelTextField(String text) {
        this(new DefaultSelDocumentModel(text), true, new ElementId(ELEMENT_ID), null);
    }
    
    public SelTextField(SelDocumentModel model) {
        this(model, true, new ElementId(ELEMENT_ID), null);
    }
    
    public SelTextField(String text, String style) {
        this(new DefaultSelDocumentModel(text), true, new ElementId(ELEMENT_ID), style);
    }
    
    public SelTextField(String text, ElementId elementId) {
        this(new DefaultSelDocumentModel(text), true, elementId, null);
    }
    
    public SelTextField(String text, ElementId elementId, String style) {
        this(new DefaultSelDocumentModel(text), true, elementId, style);
    }
    
    public SelTextField(SelDocumentModel model, String style) {
        this(model, true, new ElementId(ELEMENT_ID), style);
    }
    
    protected SelTextField(SelDocumentModel model, boolean applyStyles, ElementId elementId, String style) {
        super(false, elementId, style);

        // Set our layer ordering
        getControl(GuiControl.class).setLayerOrder(LAYER_INSETS,
                LAYER_BORDER,
                LAYER_BACKGROUND,
                LAYER_TEXT);
        
        setDocumentModel(model);
        
        addControl(new MouseEventControl(FocusMouseListener.INSTANCE));
        
        if (applyStyles) { // applyStyles is always true!
            Styles styles = GuiGlobals.getInstance().getStyles();
            styles.applyStyles(this, elementId.getId(), style);
        }
    }
    
    protected void setDocumentModel(SelDocumentModel model) {
        if (model == null) {
            return;
        }
        this.text = createTextEntryComponent(model);
        getControl(GuiControl.class).setComponent(LAYER_TEXT, text);
    }
    
    protected SelTextEntryComponent createTextEntryComponent(SelDocumentModel model) {
        Styles styles = GuiGlobals.getInstance().getStyles();
        BitmapFont font = styles.getAttributes(getElementId().getId(), getStyle()).get("font", BitmapFont.class);
        return new SelTextEntryComponent(model, font);
    }
    
    @StyleDefaults(ELEMENT_ID)
    public static void initializeDefaultStyles(Attributes attrs) {
        /*
        added 4 standard attributes to the textfield (Scrollmode, maxLinecount, Textselectmode and readonly)
        in case they are not set during styling we set the defaults at this place
        ScrollMode = no adjustements
        Maxlinecount =  by default the textfield has at least 1 line
        Textselect = we use the inbuilt select options
        readonly = we delete key input and prevent user changes on textfield except those set via style
         */
        attrs.set("background", new QuadBackgroundComponent(new ColorRGBA(0, 0, 0, 1)), false);
        attrs.set("singleLine", true, false);
        attrs.set("preferredLineCount", 1, false);
        attrs.set("Scrollmode", ScrollMode.None, false);
        attrs.set("maxLinecount", 1, false);
        attrs.set("Textselectmode", TextselectMode.Auto, false);
        attrs.set("readonly", false, false);
    }
    
    public Map<KeyAction, SelKeyActionListener> getActionMap() {
        return text.getActionMap();
    }
    
    public SelDocumentModel getDocumentModel() {
        return text.getDocumentModel();
    }
    
    @StyleAttribute(value = "text", lookupDefault = false)
    public void setText(String s) {
        text.setText(s);
    }
    
    public String getText() { // returns the currently visible text
        return text == null ? null : text.getText();
    }
    
    public String getfullText() {
        return text == null ? null : text.getfullText();
    }
    
    @StyleAttribute(value = "textVAlignment", lookupDefault = false)
    public void setTextVAlignment(VAlignment a) {
        text.setVAlignment(a);
    }
    
    public VAlignment getTextVAlignment() {
        return text.getVAlignment();
    }
    
    @StyleAttribute(value = "textHAlignment", lookupDefault = false)
    public void setTextHAlignment(HAlignment a) {
        text.setHAlignment(a);
    }
    
    public HAlignment getTextHAlignment() {
        return text.getHAlignment();
    }
    
    public void setFocusable(boolean focusable) {
        text.setFocusable(focusable);
    }
    
    @StyleAttribute("font")
    public void setFont(BitmapFont f) {
        text.setFont(f);
    }
    
    public BitmapFont getFont() {
        return text.getFont();
    }
    
    @StyleAttribute("color")
    public void setColor(ColorRGBA color) {
        text.setColor(color);
    }
    
    public ColorRGBA getColor() {
        return text == null ? null : text.getColor();
    }
    
    @StyleAttribute("fontSize")
    public void setFontSize(float f) {
        text.setFontSize(f);
    }
    
    public float getFontSize() {
        return text == null ? 0 : text.getFontSize();
    }
    
    @StyleAttribute("singleLine")
    public void setSingleLine(boolean f) {
        text.setSingleLine(f);
    }
    
    public boolean isSingleLine() {
        return text.isSingleLine();
    }
    
    @StyleAttribute("preferredWidth")
    public void setPreferredWidth(float f) {
        text.setPreferredWidth(f);
    }
    
    public float getPreferredWidth() {
        return text.getPreferredWidth();
    }
    
    @StyleAttribute("preferredLineCount")
    public void setPreferredLineCount(int i) {
        text.setPreferredLineCount(i);
        // if we dont have a max linecount yet, we set it
        if (getmaxLinecount() == 0) {
            setmaxLinecount(i);
        }
    }
    
    public int getPreferredLineCount() {
        return text.getPreferredLineCount();
    }
    
    @StyleAttribute(value = "readonly", lookupDefault = false)
    public void setreadonly(boolean readonly) {
        text.setReadonlymode(readonly);
    }
    
    public boolean getReadonlymode() {
        return text.getReadonlymode();
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[text=" + getText() + ", color=" + getColor() + ", elementId=" + getElementId() + "]";
    }
    
    @StyleAttribute("Scrollmode")
    public void setScrollMode(ScrollMode sm) {
        /*
        We have 4 different ScrollModes available in textfields
        None (0) =  No scrolling at all is allowed. The number of lines is limited by setPrefferedLinecound or setMaxlinecount
        Full (1) =  Scrolling in X-and Y direction with offset is enabled. Maxlinecount is ignored
        AutoadjustX (2) = Scrolling in Y direction, X direction is autoadjusted when the line reaches the border, Maxlinecount is ignored
        AutoadjustY (3) = Scrolling in X direction is allowed. Adding of more lines then set with setMaxlinecount is desactivated
                          the SingleLine characteristic is a special case of AutoadjustY with only 1 visible line
         */
        int smint = 0;
        switch (sm) {
            case None:
                smint = 0;
                break;
            case Full:
                smint = 1;
                break;
            case AutoadjustX:
                smint = 2;
                break;
            case AutoadjustY:
                smint = 3;
                break;
        }
        text.setScrollMode(smint);
    }
    
    @StyleAttribute("maxLinecount")
    public void setmaxLinecount(int i) {
        text.setMaxLinecount(i);
    }
    
    public int getmaxLinecount() {
        return text.getMaxLinecount();
    }
    
    public void setTextselectmode(boolean onoff) {
        if (onoff) {
            setTextselectmode(TextselectMode.Auto);
        } else {
            setTextselectmode(TextselectMode.Off);
        }
    }
    
    @StyleAttribute("Textselectmode") // 2 style attributes for same procedure is not possible
    public void setTextselectmode(TextselectMode mode) {
        /*
        3 different Textselect modes are available
        Off (0) - selected text is never shown
        Auto (1) - the textfield brings options to select and deselect text via keys and key combinations
                   additional manipulations can be done via code
        Manuell (2) - textselect options are available via code (addTextselectArea etc.)
         */
        int tmp = 0;
        switch (mode) {
            case Off:
                tmp = 0;
                break;
            case Auto:
                tmp = 1;
                break;
            case Manuell:
                tmp = 2;
                break;
        }
        text.setTxtselmodeint(tmp);
    }
    
    public TextselectMode getTextselectmode() {
        int tmp = text.getTextselectModeint();
        switch (tmp) {
            case 0:
                return TextselectMode.Off;
            case 1:
                return TextselectMode.Auto;
            case 2:
                return TextselectMode.Manuell;
        }
        return TextselectMode.Off;
    }
    
    public boolean isTextselect() {
        return text.isTextselect();
    }
    
    @StyleAttribute("TextselectColor")
    public void setSelectColor(ColorRGBA newColor) {
        text.resetSelectColor(newColor);
    }
    
    public void setSelectColor(ColorRGBA newselectColor1, ColorRGBA newselectColor2, ColorRGBA newselectColor3, ColorRGBA newselectColor4) {
        text.resetSelectColor(newselectColor1, newselectColor2, newselectColor3, newselectColor4);
    }
    
    public ColorRGBA getSelectColor1() {
        return text.getselectColor1();
    }
    
    public ColorRGBA getSelectColor2() {
        return text.getselectColor2();
    }
    
    public ColorRGBA getSelectColor3() {
        return text.getselectColor3();
    }
    
    public ColorRGBA getSelectColor4() {
        return text.getselectColor4();
    }
    
    public void adjustText(int startline, boolean fulladjust, boolean wordwrap) {
        fulladjust = !fulladjust;
        text.textadjust(startline, fulladjust, wordwrap);
    }
    
    public int[] getTextlinesbyCoordinates(int[] coordinatesXY) {
        return text.getTextlineYX(coordinatesXY);
    }

    /**
     * Sets the preferred with of the cursor quad. If set to null then the
     * default behavior is used. See
     * TextEntryComponent.setPreferredCursorWidth().
     */
    @StyleAttribute("preferredCursorWidth")
    public void setPreferredCursorWidth(Float f) {
        text.setPreferredCursorWidth(f);
    }
    
    public Float getPreferredCursorWidth() {
        return text.getPreferredCursorWidth();
    }
    
    public void actionDelete() {
        text.actionDelete();
    }
    
    public void actionCopyCut() {
        text.actionCopyCut();
    }
    
    public void actionCopySelect() {
        text.actionCopySelect();
    }
    
    public void actionPaste() {
        text.actionPaste();
    }
    
    public void actionSelectAll() {
        text.actionSelectAll();
    }
    
    public void addMenuItem(String itemText, String itemHotKey, SelTextCommand clickCommands) {
        MenuItem mi = new MenuItem(itemText, itemHotKey, clickCommands);
        if (clickCommands != null) {
            clickCommands.setSelTextField(this);
        }
        menuList.add(mi);
    }
}
