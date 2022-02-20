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
package com.simsilica.lemur.component;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import com.jme3.font.*;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;

import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiMaterial;
import com.simsilica.lemur.core.GuiUpdateListener;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.SelKeyActionListener;
import com.simsilica.lemur.event.KeyListener;
import com.simsilica.lemur.event.KeyModifiers;
import com.simsilica.lemur.event.ModifiedKeyInputEvent;
import com.simsilica.lemur.focus.FocusTarget;
import com.simsilica.lemur.focus.FocusNavigationState;
import com.simsilica.lemur.focus.FocusTraversal.TraversalDirection;
import com.simsilica.lemur.text.DefaultSelDocumentModel;
import com.simsilica.lemur.text.SelDocumentModelFilter;
import com.simsilica.lemur.text.SelDocumentModel;

/**
 * A basic text entry component that allows displaying and editing of text based
 * on a SelDocumentModel. Default key bindings are setup for common navigation
 * functions and the key input is taken over while the component has focus.
 *
 * @author Paul Speed
 * @author Aufricer
 * @author Janusch Rentenatus
 */
public class SelTextEntryComponent extends AbstractGuiComponent
        implements FocusTarget, ColoredComponent {

    public static final SelKeyActionListener DOC_HOME = new DocumentHome();
    public static final SelKeyActionListener DOC_END = new DocumentEnd();
    public static final SelKeyActionListener LINE_HOME = new LineHome();
    public static final SelKeyActionListener LINE_END = new LineEnd();
    public static final SelKeyActionListener PREV_LINE = new PreviousLine();
    public static final SelKeyActionListener NEXT_LINE = new NextLine();
    public static final SelKeyActionListener LEFT = new CaratLeft();
    public static final SelKeyActionListener RIGHT = new CaratRight();
    public static final SelKeyActionListener NO_OP = new NullAction();
    public static final SelKeyActionListener BACKSPACE = new Backspace();
    public static final SelKeyActionListener NEW_LINE = new NewLine();
    public static final SelKeyActionListener DELETE = new Delete();

    public static final SelKeyActionListener FOCUS_NEXT = new FocusChange(TraversalDirection.Next);
    public static final SelKeyActionListener FOCUS_PREVIOUS = new FocusChange(TraversalDirection.Previous);
    public static final SelKeyActionListener FOCUS_DOWN = new FocusChange(TraversalDirection.Down);
    public static final SelKeyActionListener FOCUS_UP = new FocusChange(TraversalDirection.Up);

    public static final SelKeyActionListener Shiftleft = new SelectLeft();
    public static final SelKeyActionListener ShiftRight = new SelectRight();
    public static final SelKeyActionListener ShiftUp = new SelectUp();
    public static final SelKeyActionListener ShiftDown = new SelectDown();
    public static final SelKeyActionListener ShiftLineHome = new SelectLineHome();
    public static final SelKeyActionListener ShiftLineEnd = new SelectLineEnd();
    public static final SelKeyActionListener ShiftDocHome = new SelectDocHome();
    public static final SelKeyActionListener ShiftDocEnd = new SelectDocEnd();
    public static final SelKeyActionListener STRA = new SelectAll();
    public static final SelKeyActionListener STRC = new CopySelect();
    public static final SelKeyActionListener STRX = new CopyCut();
    public static final SelKeyActionListener STRV = new Paste();
    public static final SelKeyActionListener SPACESELECT = new Space_Anchor();


    /*
    private static final Map<KeyAction,SelKeyActionListener> standardActions = new HashMap<KeyAction,SelKeyActionListener>();
    static {
        standardActions.put(new KeyAction(KeyInput.KEY_HOME), LINE_HOME);
        standardActions.put(new KeyAction(KeyInput.KEY_END), LINE_END);
        standardActions.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.CONTROL_DOWN), DOC_HOME);
        standardActions.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.CONTROL_DOWN), DOC_END);
        standardActions.put(new KeyAction(KeyInput.KEY_UP), PREV_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_DOWN), NEXT_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_LEFT), LEFT);
        standardActions.put(new KeyAction(KeyInput.KEY_RIGHT), RIGHT);
        standardActions.put(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
        standardActions.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_DELETE), DELETE);
    }
     */
    private static final Map<KeyAction, SelKeyActionListener> standardActions = new HashMap<KeyAction, SelKeyActionListener>();

    static {
        // actions that are "constant"
        standardActions.put(new KeyAction(KeyInput.KEY_HOME), LINE_HOME);
        standardActions.put(new KeyAction(KeyInput.KEY_END), LINE_END);
        standardActions.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.CONTROL_DOWN), DOC_HOME);
        standardActions.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.CONTROL_DOWN), DOC_END);

        standardActions.put(new KeyAction(KeyInput.KEY_LEFT), LEFT);
        standardActions.put(new KeyAction(KeyInput.KEY_RIGHT), RIGHT);
        standardActions.put(new KeyAction(KeyInput.KEY_TAB), FOCUS_NEXT);
        standardActions.put(new KeyAction(KeyInput.KEY_TAB, KeyModifiers.SHIFT_DOWN), FOCUS_PREVIOUS);

        standardActions.put(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
        standardActions.put(new KeyAction(KeyInput.KEY_DELETE), DELETE);

    }

    private static final Map<KeyAction, SelKeyActionListener> singleLineActions = new HashMap<KeyAction, SelKeyActionListener>();

    static {
        singleLineActions.put(new KeyAction(KeyInput.KEY_RETURN), FOCUS_NEXT);
        singleLineActions.put(new KeyAction(KeyInput.KEY_NUMPADENTER), FOCUS_NEXT);
        singleLineActions.put(new KeyAction(KeyInput.KEY_UP), FOCUS_UP);
        singleLineActions.put(new KeyAction(KeyInput.KEY_DOWN), FOCUS_DOWN);
    }

    private static final Map<KeyAction, SelKeyActionListener> multiLineActions = new HashMap<KeyAction, SelKeyActionListener>();

    static {
        multiLineActions.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
        multiLineActions.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
        multiLineActions.put(new KeyAction(KeyInput.KEY_UP), PREV_LINE);
        multiLineActions.put(new KeyAction(KeyInput.KEY_DOWN), NEXT_LINE);
    }

    private static final Map<KeyAction, SelKeyActionListener> txtselectActions = new HashMap<KeyAction, SelKeyActionListener>();

    static {
        txtselectActions.put(new KeyAction(KeyInput.KEY_LEFT, KeyModifiers.SHIFT_DOWN), Shiftleft);
        txtselectActions.put(new KeyAction(KeyInput.KEY_RIGHT, KeyModifiers.SHIFT_DOWN), ShiftRight);
        txtselectActions.put(new KeyAction(KeyInput.KEY_UP, KeyModifiers.SHIFT_DOWN), ShiftUp);
        txtselectActions.put(new KeyAction(KeyInput.KEY_DOWN, KeyModifiers.SHIFT_DOWN), ShiftDown);
        txtselectActions.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.SHIFT_DOWN), ShiftLineHome);
        txtselectActions.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.SHIFT_DOWN), ShiftLineEnd);
        txtselectActions.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.CONTROL_DOWN, KeyModifiers.SHIFT_DOWN), ShiftDocHome);
        txtselectActions.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.CONTROL_DOWN, KeyModifiers.SHIFT_DOWN), ShiftDocEnd);

        txtselectActions.put(new KeyAction(KeyInput.KEY_A, KeyModifiers.CONTROL_DOWN), STRA);
        txtselectActions.put(new KeyAction(KeyInput.KEY_C, KeyModifiers.CONTROL_DOWN), STRC);
        txtselectActions.put(new KeyAction(KeyInput.KEY_X, KeyModifiers.CONTROL_DOWN), STRX);
        txtselectActions.put(new KeyAction(KeyInput.KEY_V, KeyModifiers.CONTROL_DOWN), STRV);
        txtselectActions.put(new KeyAction(KeyInput.KEY_SPACE), SPACESELECT);

    }

    private static final Map<KeyAction, SelKeyActionListener> inputActions = new HashMap<KeyAction, SelKeyActionListener>();

    static {
        // Actions that change the input of the textfield
        inputActions.put(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
        inputActions.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
        inputActions.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
        inputActions.put(new KeyAction(KeyInput.KEY_DELETE), DELETE);
        inputActions.put(new KeyAction(KeyInput.KEY_X, KeyModifiers.CONTROL_DOWN), STRX);
        inputActions.put(new KeyAction(KeyInput.KEY_V, KeyModifiers.CONTROL_DOWN), STRV);
        inputActions.put(new KeyAction(KeyInput.KEY_SPACE), SPACESELECT);
    }

    private BitmapFont font;
    private BitmapText bitmapText;
    private Rectangle textBox;
    private HAlignment hAlign = HAlignment.Left;
    private VAlignment vAlign = VAlignment.Top;
    private Vector3f preferredSize;
    private float preferredWidth;
    private int preferredLineCount; // shown numbers of lines
    private KeyHandler keyHandler = new KeyHandler();
    private Quad cursorQuad;
    private Geometry cursor;
    private SelDocumentModel model;
    private boolean singleLine;
    private boolean focused;
    private boolean cursorVisible = true;
    private Float preferredCursorWidth = null;

    private int scrollMode;
    private int maxLinecount; //max numbers of lines
    private ColorRGBA selectorColor = new ColorRGBA(0.2f, 0.3f, 0.4f, 1f);
    private int txtselmodeint;
    private int offset_x = 0;
    private Quad textselectQuad;
    private Geometry selectbar;
    private boolean readonly = false; // prevent key input in textfields

    private VersionedReference<SelDocumentModel> modelRef;
    private VersionedReference<Integer> caratRef;
    private VersionedReference<Integer> ancRef;

    private GuiUpdateListener updateListener = new ModelChecker();

    private Map<KeyAction, SelKeyActionListener> actionMap = new HashMap<KeyAction, SelKeyActionListener>(standardActions); // map of current keybindings

    public SelTextEntryComponent(BitmapFont font) {
        this(new DefaultSelDocumentModel(), font);
    }

    public SelTextEntryComponent(SelDocumentModel model, BitmapFont font) {
        this.font = font;
        this.bitmapText = new BitmapText(font);
        bitmapText.setLineWrapMode(LineWrapMode.Clip);
        // Can't really do this since we don't know what
        // bucket it will actually end up in Gui or regular.
        //bitmapText.setQueueBucket( Bucket.Transparent );
        this.model = model;

        // Create a versioned reference for watching for updates, external or otherwise
        this.modelRef = model.createReference();
        this.caratRef = model.createCaratReference();
        this.ancRef = model.createAnchorReference();

        //   cursorQuad = new Quad(bitmapText.getLineHeight()/16f, bitmapText.getLineHeight());
        cursorQuad = new Quad(getCursorWidth(), bitmapText.getLineHeight());
        cursor = new Geometry("cursor", cursorQuad);
        GuiMaterial mat = GuiGlobals.getInstance().createMaterial(new ColorRGBA(1, 1, 1, 0.75f), false);
        cursor.setMaterial(mat.getMaterial());
        //cursor.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        cursor.setUserData("layer", 1);
        bitmapText.attachChild(cursor);

        if (model.getText() != null) {
            resetText();
        }
        if (isTextselect()) {
            makeTextselectQuads();
        }

    }

    @Override
    public SelTextEntryComponent clone() {
        SelTextEntryComponent result = (SelTextEntryComponent) super.clone();
        result.bitmapText = new BitmapText(font);
        bitmapText.setLineWrapMode(LineWrapMode.Clip);

        result.model = model.clone();
        result.preferredSize = null;
        result.textBox = null;
        result.keyHandler = result.new KeyHandler();
        result.cursorQuad = new Quad(getCursorWidth(), bitmapText.getLineHeight());
        result.cursor = new Geometry("cursor", cursorQuad);
        GuiMaterial mat = GuiGlobals.getInstance().createMaterial(new ColorRGBA(1, 1, 1, 0.75f), false);
        result.cursor.setMaterial(mat.getMaterial());
        //result.cursor.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        result.bitmapText.attachChild(cursor);
        result.resetText();

        result.readonly = readonly;
        return result;
    }

    @Override
    public void attach(GuiControl parent) {
        super.attach(parent);
        parent.addUpdateListener(updateListener);
        getNode().attachChild(bitmapText);
        resetCursorPosition();
        resetCursorState();

        if (focused) {
            GuiGlobals.getInstance().addKeyListener(keyHandler);
        }
    }

    @Override
    public void detach(GuiControl parent) {
        GuiGlobals.getInstance().removeKeyListener(keyHandler);

        getNode().detachChild(bitmapText);
        parent.removeUpdateListener(updateListener);
        super.detach(parent);
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public boolean isFocusable() {
        return true; // should return isEnabled() when we have one
    }

    @Override
    public void focusGained() {
        if (this.focused) {
            return;
        }
        this.focused = true;
        GuiGlobals.getInstance().addKeyListener(keyHandler);
        resetCursorState();
    }

    @Override
    public void focusLost() {
        if (!this.focused) {
            return;
        }
        this.focused = false;
        GuiGlobals.getInstance().removeKeyListener(keyHandler);
        resetCursorState();
    }

    public Map<KeyAction, SelKeyActionListener> getActionMap() {
        return actionMap;
    }

    public SelDocumentModel getDocumentModel() {
        return model;
    }

    public void setSingleLine(boolean f) {
        this.singleLine = f;
        if (singleLine) {
            //        actionMap.put(new KeyAction(KeyInput.KEY_RETURN), FOCUS_NEXT);
            //        actionMap.put(new KeyAction(KeyInput.KEY_NUMPADENTER), FOCUS_NEXT);
            //        actionMap.put(new KeyAction(KeyInput.KEY_TAB), FOCUS_NEXT);
            //        actionMap.put(new KeyAction(KeyInput.KEY_TAB, KeyModifiers.SHIFT_DOWN), FOCUS_PREVIOUS);
            //        actionMap.put(new KeyAction(KeyInput.KEY_UP), FOCUS_UP);
            //        actionMap.put(new KeyAction(KeyInput.KEY_DOWN), FOCUS_DOWN);

            // scrollMode and maxLines are reset, thus reset offsets as well
            preferredLineCount = 1;
            model.setmaxlines(1);
            maxLinecount = 1;
            setScrollMode(3);
            offset_x = 0;
            model.setOffset_X(offset_x);

        } //else {

        //      actionMap.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
        //      actionMap.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
        // We may choose to do something different with tab someday... but
        // the user can also just remove the action if they like.
        //      actionMap.put(new KeyAction(KeyInput.KEY_TAB), FOCUS_NEXT);
        //      actionMap.put(new KeyAction(KeyInput.KEY_TAB, KeyModifiers.SHIFT_DOWN), FOCUS_PREVIOUS);
        //     actionMap.put(new KeyAction(KeyInput.KEY_UP), PREV_LINE);
        //     actionMap.put(new KeyAction(KeyInput.KEY_DOWN), NEXT_LINE);
        //  }
        updatekeybindings();
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public void setFont(BitmapFont font) {
        if (font == bitmapText.getFont()) {
            return;
        }

        if (isAttached()) {
            bitmapText.removeFromParent();
        }

        // Can't change the font once created so we'll
        // have to create it fresh
        BitmapText newText = new BitmapText(font);
        newText.setLineWrapMode(LineWrapMode.Clip);
        newText.setText(getText());
        newText.setColor(getColor());
        newText.setLocalTranslation(bitmapText.getLocalTranslation());
        newText.setSize(getFontSize());
        this.bitmapText = newText;

        // The cursor is attached to the bitmap text directly
        // so we need to move it.
        bitmapText.attachChild(cursor);

        // we also need to change the font! as the font parameter is used in getVisibleWidth()
        this.font = font;

        resizeCursor();
        resetCursorPosition();
        resetText();

        if (isAttached()) {
            getNode().attachChild(bitmapText);
        }
    }

    public BitmapFont getFont() {
        return bitmapText.getFont();
    }

    public void setFontSize(float f) {
        this.bitmapText.setSize(f);
        resizeCursor();
        resetCursorPosition();
        resetText();
    }

    public float getFontSize() {
        return bitmapText.getSize();
    }

    protected void resetCursorColor() {
        float alpha = bitmapText.getAlpha();
        if (alpha == -1) {
            alpha = 1;
        }
        ColorRGBA color = bitmapText.getColor();

        if (alpha == 1) {
            cursor.getMaterial().setColor("Color", color);
        } else {
            ColorRGBA cursorColor = color != null ? color.clone() : ColorRGBA.White.clone();
            cursorColor.a = alpha;
            cursor.getMaterial().setColor("Color", cursorColor);
        }
    }

    @Override
    public void setColor(ColorRGBA color) {
        float alpha = bitmapText.getAlpha();
        bitmapText.setColor(color);
        if (alpha != 1) {
            bitmapText.setAlpha(alpha);
        }
        resetCursorColor();
    }

    @Override
    public ColorRGBA getColor() {
        return bitmapText.getColor();
    }

    @Override
    public void setAlpha(float f) {
        bitmapText.setAlpha(f);
        resetCursorColor();
    }

    @Override
    public float getAlpha() {
        return bitmapText.getAlpha();
    }

    protected void resetText() {
        // offset x is calculated in the TextEntrycomponent we just always pass it to
        // the documentModel to make sure we get the correct composite/text back
        model.setOffset_X(offset_x);
        String text = model.getText();
        if (text != null && text.equals(bitmapText.getText())) {
            return;
        }
        bitmapText.setText(text);
        resetCursorPosition();
        invalidate();
    }

    protected float getVisibleWidth(String text) {
        // We add an extra space to properly advance (since often
        // the space character only has a width of 1 but will advance
        // far) then we subtract that space width back.
        float x = font.getLineWidth(text + " ");
        x -= font.getLineWidth(" ");
        // And pad it out just a bit...
        //x += 1;
        float scale = bitmapText.getSize() / font.getPreferredSize();

        x *= scale;
        return x;
    }

    public void setPreferredCursorWidth(Float f) {
        this.preferredCursorWidth = f;
        resizeCursor();
        resetCursorPosition();
    }

    public Float getPreferredCursorWidth() {
        return preferredCursorWidth;
    }

    public float getCursorWidth() {
        if (preferredCursorWidth != null) {
            return preferredCursorWidth;
        }
        // Because small cursor widths sometimes make the cursor invisible
        // for some reason, we'll try to detect the cases where pixels = units
        // and make sure that the width is never smaller than 1 pixel.
        float height = bitmapText.getLineHeight();
        if (height > 5) {
            return Math.max(1, height / 16f);
        }
        return height / 16f;
    }

    protected void resizeCursor() {
        cursorQuad.updateGeometry(getCursorWidth(), bitmapText.getLineHeight());
        cursorQuad.clearCollisionData();
    }

    protected void resetCursorState() {
        if (isAttached() && focused && cursorVisible) {
            cursor.setCullHint(CullHint.Inherit);
        } else {
            cursor.setCullHint(CullHint.Always);
        }
    }

    protected void resetCursorPosition() {
        // Find the current cursor position.
        int line = model.getCaratLine();
        int column = model.getCaratColumn();
        int adder = 0;

        if (column < offset_x) {
            offset_x = column;
            resetText();
        }

        String row = model.getLine(line);

        if (textBox != null) {
            // if textbox exist we need to check first if the line needs to be adjusted
            if (scrollMode == 2 && (getVisibleWidth(row) > textBox.width)) {
                textadjust(model.getCaratLine(), true, true);
                return;
            }
        }

        // we need only the offset row
        row = row.substring(offset_x, column);
        float x = getVisibleWidth(row);
        float y = (-line + model.getOffset_Y()) * bitmapText.getLineHeight();
        y -= bitmapText.getLineHeight();

        // we check if textBox exist, if not we just set our cursor as the element will be stretched
        if (textBox == null) {
            cursorVisible = true;
            resetCursorState();
            cursor.setLocalTranslation(x, y, 0.01f);
            // Optional - s. Forum https://hub.jmonkeyengine.org/t/lemur-textfield-invisible/36363/26?u=aufricer
            // cursor.setLocalTranslation(x - getCursorWidth() * 0.5f, y, 0.01f);
            return;
        }

        // we check if the cursor is out of visible field
        if (x > textBox.width) {
            if (singleLine || scrollMode == 3 || scrollMode == 1) {
                // if single line is selected and cursor went back it was possible that the text field was
                // stretched and stretched. Therefore we will set a preferred size/width the first time we have an offset
                //     if (getPreferredSize() == null) { setPreferredSize(new Vector3f(textBox.width,bitmapText.getLineHeight()*maxLinecount,0));); }
                if (preferredWidth == 0) {
                    setPreferredWidth(textBox.width);
                    System.out.println("textfield width set automatically with :" + textBox.width);
                }
                // Then we can move the text offset and try again
                // Calculation of offset x by adding 1 and recalling this function causes
                // stack overflow errors, once longer texts are added to a line (e.g. 2.000 words at once)
                // by calculating the offset directly in a loop, based on current offset the
                // recursive calls of the whole function can be reduced to a few,
                // which is still not perfect, but works OK (tested with adding 120.000 characters at once)
                do {
                    adder++;
                } while (getVisibleWidth(row.substring(adder, column - offset_x)) > textBox.width);
                offset_x += adder;
                resetText();
                resetCursorPosition();
                return;
            } else if (scrollMode == 2) {
                // depending on scrollMode we may just adjust this line again
                textadjust(model.getCaratLine(), true, true);
                return;
            } else {
                // Make it invisible in all other cases
                cursorVisible = false;
                resetCursorState();
            }
        } else { // make it visible once the cursor is inside the textfield, this may happen after several calls of this function
            cursorVisible = true;
            resetCursorState();
        }
        // finally set the right position of the cursor (inside the textfield)
        cursor.setLocalTranslation(x, y, 0.01f);
    }

    public void setText(String text) {
        if (text != null && text.equals(model.getfulltext())) {
            return;
        }

        model.setText(text);
        //resetText();  ...should be automatic now
    }

    public String getText() {
        // returns the currently visible text, not the full text
        return model.getText();
    }

    public void setHAlignment(HAlignment a) {
        if (hAlign == a) {
            return;
        }
        hAlign = a;
        resetAlignment();
    }

    public HAlignment getHAlignment() {
        return hAlign;
    }

    public void setVAlignment(VAlignment a) {
        if (vAlign == a) {
            return;
        }
        vAlign = a;
        resetAlignment();
    }

    public VAlignment getVAlignment() {
        return vAlign;
    }

    public void setPreferredSize(Vector3f v) {
        this.preferredSize = v;
        invalidate();
    }

    public Vector3f getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredWidth(float f) {
        this.preferredWidth = f;
        invalidate();
    }

    public float getPreferredWidth() {
        return preferredWidth;
    }

    public void setPreferredLineCount(int i) {
        this.preferredLineCount = Math.max(i, 1);
        invalidate();
    }

    public int getPreferredLineCount() {
        return preferredLineCount;
    }

    @Override
    public void reshape(Vector3f pos, Vector3f size) {
        bitmapText.setLocalTranslation(pos.x, pos.y, pos.z + 0.009f);
        textBox = new Rectangle(0, 0, size.x, size.y);
        bitmapText.setBox(textBox);
        resetAlignment();
    }

    @Override
    public void calculatePreferredSize(Vector3f size) {
        if (preferredSize != null) {
            size.set(preferredSize);
            return;
        }

        // Make sure that the bitmapText reports a reliable
        // preferred size
        bitmapText.setBox(null);

        if (preferredWidth == 0) {
            size.x = bitmapText.getLineWidth();
        } else {
            size.x = preferredWidth;
        }
        if (preferredLineCount == 0) {
            size.y = bitmapText.getHeight();
        } else {
            size.y = bitmapText.getLineHeight() * preferredLineCount;
        }

        // Reset any text box we already had
        bitmapText.setBox(textBox);
    }

    protected void resetAlignment() {
        if (textBox == null) {
            return;
        }

        switch (hAlign) {
            case Left:
                bitmapText.setAlignment(Align.Left);
                break;
            case Right:
                bitmapText.setAlignment(Align.Right);
                break;
            case Center:
                bitmapText.setAlignment(Align.Center);
                break;
        }
        switch (vAlign) {
            case Top:
                bitmapText.setVerticalAlignment(VAlign.Top);
                break;
            case Bottom:
                bitmapText.setVerticalAlignment(VAlign.Bottom);
                break;
            case Center:
                bitmapText.setVerticalAlignment(VAlign.Center);
                break;
        }
    }

    private static class DocumentHome implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.home(false);
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }

        }
    }

    private static class LineHome implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.home(true);
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }
        }
    }

    private static class DocumentEnd implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.end(false);
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }
        }
    }

    private static class LineEnd implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.end(true);
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }

        }
    }

    private static class PreviousLine implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.up();
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }

        }
    }

    private static class NextLine implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.down();
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }

        }
    }

    private static class CaratLeft implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.left();
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }
        }
    }

    private static class CaratRight implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.right();
            //source.resetCursorPosition(); should be automatic now
            if (source.txtselmodeint == 1) {
                source.model.emptyAnchors();
            }
        }
    }

    private static class NullAction implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
        }
    }

    private static class Backspace implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {

            if (source.isTextselect()) {
                if (source.model.deleteSelectedText(true)) {
                    source.model.backspace();
                };
            } else {
                source.model.backspace();
            }

        }
    }

    private static class NewLine implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            if (source.isTextselect()) {
                source.model.deleteSelectedText(false);
            }
            source.model.insertNewLine();
            //source.resetText(); // should be automic now
        }
    }

    private static class Delete implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.actionDelete();
        }
    }

    public void actionDelete() {
        if (isTextselect()) {
            if (model.deleteSelectedText(false)) {
                model.delete();
            }
        } else {
            model.delete();
        }
    }

    private static class FocusChange implements SelKeyActionListener {

        private TraversalDirection dir;

        public FocusChange(TraversalDirection dir) {
            this.dir = dir;
        }

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            FocusNavigationState nav = GuiGlobals.getInstance().getFocusNavigationState();
            if (nav == null) {
                return;
            }
            Spatial current = GuiGlobals.getInstance().getCurrentFocus();
            nav.requestChangeFocus(current, dir);
        }
    }

    private class KeyHandler implements KeyListener {

        private boolean shift = false;
        private boolean control = false;

        @Override
        public void onKeyEvent(KeyInputEvent evt) {
            ModifiedKeyInputEvent mEvt = (ModifiedKeyInputEvent) evt;
            if (mEvt.isPressed()) {
                KeyAction key = mEvt.toKeyAction(); //new KeyAction(code, (control?KeyAction.CONTROL_DOWN:0), (shift?KeyAction.SHIFT_DOWN:0) );
                SelKeyActionListener handler = actionMap.get(key);
                if (handler != null) {
                    handler.keyAction(SelTextEntryComponent.this, key);
                    evt.setConsumed();
                    return;
                }

                // Making sure that no matter what, certain
                // characters never make it directly to the
                // document
                if ((evt.getKeyChar() >= 32) && (!(readonly))) { // prevent keyinput
                    if (txtselmodeint == 1) {
                        model.deleteSelectedText(false);
                        model.emptyAnchors();
                    }
                    model.insert(evt.getKeyChar());
                    evt.setConsumed();
                }
            }
        }
    }

    /**
     * Checks for changes in the model and updates the text display or cursor
     * position as necessary.
     */
    // Das GUICONTROL hat in seinem Update die Funktion GUIUPDATEListener
    // Der UpdateListener wurde hinzugefuegt (nur fuer TextEntryComponenten)
    // laufen die Updates, laufen auch die ControlUpdates
    // Das GUI Control ist ein Control
    // Somit laeuft bei den TextEntryComponenten dieses Update
    // Bei den anderen Elementen nicht!
    private class ModelChecker implements GuiUpdateListener {

        @Override
        public void guiUpdate(GuiControl source, float tpf) {
            if (modelRef.update()) { // TextModell changed
                resetText();
                if (isTextselect()) {
                    makeTextselectQuads();
                    ancRef.update();
                }
            }
            if (caratRef.update()) {  // cursorPosition changed
                resetCursorPosition();
            }

            if (ancRef.update()) { // Anchors changed
                if (isTextselect()) {
                    makeTextselectQuads();
                }
            }
        }
    }

    // Added start
    private static class SelectLeft implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.left();
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectRight implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.right();
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectUp implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.up();
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectDown implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.down();
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectLineEnd implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.end(true);
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectLineHome implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.home(true);
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectDocHome implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.home(false);
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectDocEnd implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            int z = source.model.getCarat();
            source.model.end(false);
            source.model.reverseSelect(z, source.model.getCarat());
        }
    }

    private static class SelectAll implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.actionSelectAll();
        }
    }

    public void actionSelectAll() {
        model.end(false);
        model.emptyAnchors();
        model.addTextselectArea(0, model.getCarat());
    }

    private static class CopySelect implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.actionCopySelect();
        }
    }

    public void actionCopySelect() {
        Pasteto(model.getselectedText());
    }

    private static class CopyCut implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.actionCopyCut();
        }
    }

    public void actionCopyCut() {
        Pasteto(model.getselectedText());
        model.deleteSelectedText(false);
    }

    private static class Paste implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.actionPaste();
        }
    }

    public void actionPaste() {
        model.deleteSelectedText(false);
        model.emptyAnchors();

        try {
            model.insert(Copyfrom());
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textadjust(model.getCaratLine(), true, false);
    }

    private static class Space_Anchor implements SelKeyActionListener {

        @Override
        public void keyAction(SelTextEntryComponent source, KeyAction key) {
            source.model.insert(" ");
            if (source.isTextselect()) {
                source.model.emptyAnchors();
            }
        }
    }

    public void setMaxLinecount(int maxLinecount) {
        this.maxLinecount = maxLinecount;
        setPreferredLineCount(Math.max(1, getPreferredLineCount()));
        if (!(maxLinecount == 0)) {
            // maxLinecount <= preferredLineCount
            this.maxLinecount = Math.min(maxLinecount, preferredLineCount);
        }
        model.setmaxlines(this.maxLinecount);
    }

    public int getMaxLinecount() {
        return this.maxLinecount;
    }

    public int getScrollMode() {
        return scrollMode;
    }

    public void setScrollMode(int scrollMode) {
        // (0 = None, 1 = Full (X + Y), 2 = Y yes, X will be  auto adjustet, 3 = Y no, X yes (as single line, but with more lines) )
        this.scrollMode = scrollMode;
        if (scrollMode == 0 || scrollMode == 3) {
            setMaxLinecount(Math.max(1, getMaxLinecount()));
        }
        model.setScrollMode(scrollMode);
        offset_x = 0;
        model.setOffset_X(offset_x);
    }

    public String getoffsetText() {
        return model.getoffsetText(new int[]{0, model.getOffset_Y()}, true);
    }

    public void textadjust(int startline, boolean oneline, boolean wordadjust) {
        // some parameters we will just need for adjusting our text
        String newText = "";
        int j;
        int i;
        int lng = 0;
        int maxVar;
        String crow;
        boolean tester = true;
        int z = 0;
        double tlng;
        int k;

        if (textBox == null) {
            return; //     in case we call this before the GUIComponent Update was run and textbox exist
        }
        /*
        AdjustText worked fine for DefaultDocumentModels for DocumentModelFilter we needed to do a few adjustements
        - textmodel will always contain the unfiltered text
        - sometimes we still use model and not textmodel as often the delegate.function is called in DocumentModelFilter
        We still have the problem, that the (visible) length of filtered output can be different from unfiltered
        therefore we check the filtered output here but take our new text from unfiltered
        not sure if this is bug free but it seems it is working
        We also estimate that the text of DefaultDocumentModel and DocumentModelfilter always equals in lenght
        A todo would be to check and refactor this function
         */
        SelDocumentModel textmodel = model;
        String unfilteredrow;

        if (model instanceof SelDocumentModelFilter) {
            textmodel = ((SelDocumentModelFilter) model).getDelegate();
            if (!(model.getfulltext().length() == textmodel.getfulltext().length())) {
                return; //Upps, should not happen
            }
        }

        // text until startposition will be unchanged
        if (startline + 1 > textmodel.getLineCount()) {
            return;
        }
        for (j = 0; j <= startline - 1; j++) {
            lng += textmodel.getLine(j).length();
            //   lng += model.getLine(j).length();
        }
        lng = lng + startline;

        newText = new StringBuilder(textmodel.getoffsetText(new int[]{0, 0}, false)).substring(0, lng);
        //  newText = new StringBuilder(model.getoffsetText(new int[] {0,0},false)).substring(0,lng).toString();

        crow = model.getLine(j);
        unfilteredrow = textmodel.getLine(j);

        j++;
        tlng = lng;

        do { // all lines or just 1 line will be adjusted
            tester = true;
            do { // adjusting a single line
                lng = crow.length();
                i = 0;

                // we approach from left side, otherwise the calculation time while huge texts are inserted is extremely high
                while ((getVisibleWidth(crow.substring(0, i)) < textBox.width) && i < lng) {
                    i++;
                }

                if ((i == lng) && getVisibleWidth(crow.substring(0, i)) < textBox.width) {
                    i++;
                }

                /*
                if somehow the textbox has no width or is smaller then even 1 letter the following wordadjust
                functionality would lead to a null pointer exception. to prevent this we could always check if
                getVisibleWidth(" ") < textbox.width but by checking the number of letters we are more accurate
                as we also need the number of letters that are beyond the textbox border we use this calculation
                to check and break the functionality if necessary
                 */
                i = crow.length() - i + 1; // that is the number of letters that are out of the boarder of the textbox
                if (i >= lng) {
                    break; // if all letters are out we can not adjust anything and jump to appending the remaining lines as they are
                }

                // if we adjust words, 10% of visible space line is given to wrap words instead of characters
                // a word starts where an " " is found before the word
                if (wordadjust) {
                    maxVar = (int) (Math.floor(lng - i) * 0.1);
                    for (k = 0; k < maxVar + 1; k++) {
                        //  String kk = crow.substring(crow.length()-i-k-1,crow.length()-i-k);
                        if ((crow.substring(crow.length() - i - k - 1, crow.length() - i - k).codePointAt(0) == 32)) {
                            break;
                        }
                    }
                    if (k > maxVar) {
                        k = 0;
                    }
                    i += k;
                }

                newText = new StringBuilder(newText).append(unfilteredrow.substring(0, unfilteredrow.length() - i) + "\n").toString();
                //     newText = new StringBuilder(newText).append(crow.substring(0, crow.length() - i) + "\n").toString();

                tlng += crow.length() - i;
                // potential additional line breaks change the position of the carat
                // as startline maybe different from cursor line only valid line breaks are counted
                if (tlng < model.getCarat() && model.getCaratLine() < j) {
                    z++;
                }

                if (j < model.getLineCount() && getVisibleWidth(new StringBuilder(crow.substring(lng - i)).toString()) < textBox.width) {
                    crow = new StringBuilder(crow.substring(lng - i)).append(model.getLine(j)).toString(); //filtered content
                    unfilteredrow = new StringBuilder(unfilteredrow.substring(lng - i)).append(textmodel.getLine(j)).toString();
                    j++;
                } else {
                    crow = new StringBuilder(crow.substring(lng - i)).toString();
                    unfilteredrow = new StringBuilder(unfilteredrow.substring(lng - i)).toString();
                }

                if (getVisibleWidth(crow) < textBox.width) {
                    tester = false;
                }
            } while (tester);
        } while ((j < startline) || (!oneline && j < model.getLineCount()));

        // adding to the last line
        newText = new StringBuilder(newText).append(unfilteredrow).toString();
        //newText = new StringBuilder(newText).append(crow).toString();

        // remaining lines wont be adjusted and added
        for (i = j; i < model.getLineCount(); i++) {
            newText = new StringBuilder(newText).append("\n" + textmodel.getLine(i)).toString();
            //   newText = new StringBuilder(newText).append("\n"+model.getLine(i)).toString();
        }

        // the setText function unfort. set our cursor at the end of the text
        // this may also result in an unwanted Y-offset
        // to avoid this we have to reset the cursor
        i = model.getCarat();
        textmodel.setText(newText);
        model.updateCarat(true, i + z, false);

        // adjustements in case we have a scrollMode with limited line numbers
        if (!(scrollMode == 1 || scrollMode == 3 || singleLine)) {
            offset_x = 0;
        }
        if ((scrollMode == 0 || singleLine || scrollMode == 3) && getMaxLinecount() < model.getLineCount()) {

            lng = 0;
            z = 0;
            for (j = 0; j < getMaxLinecount(); j++) {
                lng += textmodel.getLine(j).length();
            }

            lng = lng + j - 1;
            newText = new StringBuilder(textmodel.getoffsetText(new int[]{0, 0}, false)).substring(0, lng);
            //  newText = new StringBuilder(model.getoffsetText(new int[] {0,0},false)).substring(0,lng).toString();

            String hinzu = "";
            for (i = j; i < model.getLineCount(); i++) {
                hinzu = new StringBuilder(hinzu).append(textmodel.getLine(i)).toString();
            }

            if (scrollMode == 2) {
                /*
                  in this rare case we have a combination that comes with limited lines and limitation of available space in x-direction too
                  This could lead to recursive, repeating calls
                  Therefore we are checking and shortening (if necessary) the content of the textfield
                 */
                String hilfe = textmodel.getLine(j - 1); // last non appended line
                hilfe += hinzu;
                i = 0;
                lng = hilfe.length();
                while ((getVisibleWidth(hilfe.substring(0, i)) < textBox.width) && i < lng) {
                    i++;
                }
                i--;
                if (i <= textmodel.getLine(j - 1).length()) {
                    // last line must be shorten
                    hinzu = "";
                    newText = newText.substring(0, i);
                }
                if (i > textmodel.getLine(j - 1).length()) {
                    // the adding part must be reduced
                    hinzu = hinzu.substring(0, i - textmodel.getLine(j - 1).length());
                }
            }

            newText = new StringBuilder(newText).append(hinzu).toString();
            //  for (i = j; i < model.getLineCount();i++) {
            //      newText = new StringBuilder(newText).append(textmodel.getLine(i)).toString();
            //  }
            textmodel.setText(newText);
            model.updateCarat(true, 0, true);

        }
    }

    private void makeTextselectQuads() {
        /*
        decided to make the Quads with an extra Node that can be just detached and not with userdata (as in listboxes)
        but still each added textselect bar has its unique userdata
         */
        Node selectorNode = new Node();
        List<int[]> anc;
        int i;
        int[] values;
        int[] pos1 = {0, 0};
        int[] pos2 = {0, 0};
        float xstart;
        float xende;
        int j;
        float y;
        int z = 0;

        removTextselectQuads();

        // we get our field of anchors
        if (model.getAnchors() == null) {
            return;
        }
        anc = model.getAnchors();
        // some general settings
        selectorNode.setName("selectorNode");
        GuiMaterial mat = GuiGlobals.getInstance().createMaterial(selectorColor, false);

        if (textBox == null) {
            // getDocumentModel().setText(getDocumentModel().getfullText());
            return;
        }

        // each anchor is an independent textselect area
        // we find the position in text
        for (i = 0; i < anc.size(); i++) {
            values = anc.get(i);
            model.findPosition(values[0], pos1);
            model.findPosition(values[1], pos2);
            //       System.out.println("Position 1:" +  values[0] +" = line "+ pos1[0] + " column " + pos1[1]);
            //       System.out.println("Position 2:" +  values[1] +" = line "+ pos2[0] + " column " + pos2[1]);
            if (pos2[0] >= model.getLineCount()) {
                anc.remove(i);
                continue;
            }
            // and check if the textselect area is stretched over more then one line
            // lines that are out of sight, due to offset will be ignored
            for (j = pos1[0]; j <= pos2[0]; j++) {
                if ((j < model.getOffset_Y()) || (j - model.getOffset_Y() >= maxLinecount)) {
                    continue;
                }
                y = (-j + model.getOffset_Y()) * bitmapText.getLineHeight();
                y -= bitmapText.getLineHeight();

                if (offset_x > model.getLine(j).length()) {
                    continue;
                }

                if (j == pos1[0]) {
                    xstart = getVisibleWidth(model.getLine(j).substring(offset_x, Math.max(offset_x, pos1[1])).toString());
                } else {
                    xstart = 0;
                }

                if (pos1[0] == pos2[0]) { // 1 line only
                    if (offset_x > pos2[1]) {
                        continue;
                    }
                    xende = getVisibleWidth(model.getLine(j).toString().substring(offset_x, pos2[1]));
                } else {
                    if (j == pos2[0]) { // end line
                        if (offset_x > pos2[1]) {
                            continue;
                        }
                        xende = getVisibleWidth(model.getLine(j).toString().substring(offset_x, pos2[1]));
                    } else { // any line in between
                        xende = getVisibleWidth(model.getLine(j).toString().substring(offset_x, model.getLine(j).length()));
                    }
                }

                xende = Math.min(xende, textBox.width);
                if (xstart == xende) {
                    continue;
                }

                textselectQuad = new Quad(xende - xstart, bitmapText.getLineHeight());
                selectbar = new Geometry("selectbar" + z, textselectQuad);
                z++;
                selectbar.setMaterial(mat.getMaterial());
                //mat.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                selectbar.setUserData("SelectorNumber", z);
                selectorNode.attachChild(selectbar);
                selectbar.setLocalTranslation(xstart, y, -0.006f);

            }
        }
        bitmapText.attachChild(selectorNode);
    }

    public void resetSelectColor(ColorRGBA newselectColor) {
        selectorColor = new ColorRGBA(newselectColor.getRed(), newselectColor.getGreen(), newselectColor.getBlue(), selectorColor.getAlpha());
        makeTextselectQuads();
    }

    public ColorRGBA getselectColor() {
        return selectorColor;
    }

    private void removTextselectQuads() {
        // we just detach the Node we made for textselect Quads
        bitmapText.detachChildNamed("selectorNode");
    }

    public boolean isTextselect() {
        if ((txtselmodeint == 1) || (txtselmodeint == 2)) {
            return true;
        } else {
            return false;
        }
    }

    public void setTxtselmodeint(int textselectmode) {
        // 0 = None
        // 1 = Auto
        // 2 = Manuell
        this.txtselmodeint = textselectmode;
        setScrollMode(scrollMode); // to reset offset X
        if (isTextselect()) {
            model.emptyAnchors();
        } else {
            removTextselectQuads();
        }

        updatekeybindings(); // to add or update the necessary keybindings
/*
        if (txtselmodeint ==1) {
            txtselectActions.forEach(actionMap::put);
            /*
            actionMap.put(new KeyAction(KeyInput.KEY_LEFT , KeyModifiers.SHIFT_DOWN), Shiftleft);
            actionMap.put(new KeyAction(KeyInput.KEY_RIGHT , KeyModifiers.SHIFT_DOWN), ShiftRight);
            actionMap.put(new KeyAction(KeyInput.KEY_UP , KeyModifiers.SHIFT_DOWN), ShiftUp);
            actionMap.put(new KeyAction(KeyInput.KEY_DOWN , KeyModifiers.SHIFT_DOWN), ShiftDown);
            actionMap.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.SHIFT_DOWN), ShiftLineHome);
            actionMap.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.SHIFT_DOWN), ShiftLineEnd);
            actionMap.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.CONTROL_DOWN, KeyModifiers.SHIFT_DOWN), ShiftDocHome);
            actionMap.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.CONTROL_DOWN,KeyModifiers.SHIFT_DOWN), ShiftDocEnd);
            actionMap.put(new KeyAction(KeyInput.KEY_A, KeyModifiers.CONTROL_DOWN), STRA);
            actionMap.put(new KeyAction(KeyInput.KEY_C, KeyModifiers.CONTROL_DOWN), STRC);
            actionMap.put(new KeyAction(KeyInput.KEY_X, KeyModifiers.CONTROL_DOWN), STRX);
            actionMap.put(new KeyAction(KeyInput.KEY_V, KeyModifiers.CONTROL_DOWN), STRV);
            actionMap.put(new KeyAction(KeyInput.KEY_SPACE), SPACESELECT);
        } else {
            actionMap.remove(new KeyAction(KeyInput.KEY_LEFT , KeyModifiers.SHIFT_DOWN), Shiftleft);
            actionMap.remove(new KeyAction(KeyInput.KEY_RIGHT , KeyModifiers.SHIFT_DOWN), ShiftRight);
            actionMap.remove(new KeyAction(KeyInput.KEY_UP , KeyModifiers.SHIFT_DOWN), ShiftUp);
            actionMap.remove(new KeyAction(KeyInput.KEY_DOWN , KeyModifiers.SHIFT_DOWN), ShiftDown);
            actionMap.remove(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.SHIFT_DOWN), ShiftLineHome);
            actionMap.remove(new KeyAction(KeyInput.KEY_END, KeyModifiers.SHIFT_DOWN), ShiftLineEnd);
            actionMap.remove(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.CONTROL_DOWN, KeyModifiers.SHIFT_DOWN), ShiftDocHome);
            actionMap.remove(new KeyAction(KeyInput.KEY_END, KeyModifiers.CONTROL_DOWN,KeyModifiers.SHIFT_DOWN), ShiftDocEnd);
            actionMap.remove(new KeyAction(KeyInput.KEY_A, KeyModifiers.CONTROL_DOWN), STRA);
            actionMap.remove(new KeyAction(KeyInput.KEY_C, KeyModifiers.CONTROL_DOWN), STRC);
            actionMap.remove(new KeyAction(KeyInput.KEY_X, KeyModifiers.CONTROL_DOWN), STRX);
            actionMap.remove(new KeyAction(KeyInput.KEY_V, KeyModifiers.CONTROL_DOWN), STRV);
            actionMap.remove(new KeyAction(KeyInput.KEY_SPACE), SPACESELECT);
            actionMap.keySet().removeAll(txtselectActions.keySet());
        } */

    }

    public int getTextselectModeint() {
        // get the internal int of textselect mode
        return this.txtselmodeint;
    }

    private void Pasteto(String copyme) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection Text = new StringSelection(copyme);
        c.setContents(Text, Text);
    }

    private String Copyfrom() throws UnsupportedFlavorException, IOException {
        String cb;
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable t = c.getContents(null);
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            cb = (String) t.getTransferData(DataFlavor.stringFlavor);
        } else {
            cb = "";
        }
        return cb;
    }

    public int[] getTextlineYX(int[] coordinatesXY) {
        int theposition[] = {0, 0};
        float y;
        int i = 0;
        boolean last = false;

        float xmin = bitmapText.getWorldTranslation().getX();
        float xmax = textBox.width * bitmapText.getWorldScale().x + xmin;

        float ymin = bitmapText.getWorldTranslation().getY();
        float ymax = ymin - textBox.height * bitmapText.getWorldScale().y;

        // the coordinates are not inside the textbox
        if ((!(coordinatesXY[0] >= xmin && coordinatesXY[0] <= xmax)) || (!(coordinatesXY[1] >= ymax && coordinatesXY[1] <= ymin))) {
            return null;
        }

        /*
         maxlinecount or preferedlinecount is not defined in order to not destroy the element by setting any maxlinecount
         and thus preferedlinecount we just break the function here
         */
        if (maxLinecount <= 0) {
            // ToDo Make a small warning
            return null;
        }

        // get line first
        y = ymin - bitmapText.getLineHeight() * bitmapText.getWorldScale().y;

        while (y >= ymax && y > coordinatesXY[1]) {
            y -= bitmapText.getLineHeight() * bitmapText.getWorldScale().y;
            i++;
        }
        // out of visible lines
        if (i > maxLinecount - 1) {
            last = true;
            i = maxLinecount - 1;
        }
        i = i + model.getOffset_Y();
        if (i + 1 > model.getLineCount()) {
            i = model.getLineCount() - 1;
            last = true;
        }
        theposition[0] = i;
        // if we have an offset and the line is not shown or we have clicked
        // below the last line we get the end of the choosen line
        if ((offset_x > model.getLine(i).length()) || last) {
            theposition[1] = model.getLine(i).length();
        } else { // otherwise we iterate till the end of our coordinate or textrow
            String row = model.getLine(i).substring(offset_x);
            for (i = 0; i < row.length(); i++) {
                if (getVisibleWidth(row.substring(0, i)) * bitmapText.getWorldScale().x + xmin >= coordinatesXY[0]) {
                    break;
                }
            }
            theposition[1] = i + offset_x;
        }
        return theposition;
    }

    public String getfullText() {
        return model.getfulltext();
    }

    public void setReadonlymode(boolean ro) {
        this.readonly = ro;
        updatekeybindings();
        // we delete all unessesary actions
        /*
        if (this.readonly) {
            actionMap.remove(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
            actionMap.remove(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
            actionMap.remove(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
            actionMap.remove(new KeyAction(KeyInput.KEY_DELETE), DELETE);
            actionMap.keySet().removeAll(standardActions.keySet());
            standardActions.forEach(actionMap::putIfAbsent);
            actionMap.putIfAbsent(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
        } else {
            actionMap.put(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
            actionMap.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
            actionMap.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
            actionMap.put(new KeyAction(KeyInput.KEY_DELETE), DELETE);
        } */
    }

    public boolean getReadonlymode() {
        return this.readonly;
    }

    private void updatekeybindings() {
        standardActions.forEach(actionMap::putIfAbsent); //add standard actions if not yet done
        if (isSingleLine()) {
            singleLineActions.forEach(actionMap::put); // update or set singleline actions
        } else {
            multiLineActions.forEach(actionMap::put);  // update or set multiLine actions
        }
        if (txtselmodeint == 1) { // set or update textselect keybindings
            // activate user keybinding
            txtselectActions.forEach(actionMap::put);
        } else {
            actionMap.keySet().removeAll(txtselectActions.keySet());
            // user is not able to create or modify anchors via keys but they can be updated via code and are shown
            // as long as textselecmode is not 0
        }
        if (readonly) {
            actionMap.keySet().removeAll(inputActions.keySet()); // delete inputActions if necessary
        }
    }

}
