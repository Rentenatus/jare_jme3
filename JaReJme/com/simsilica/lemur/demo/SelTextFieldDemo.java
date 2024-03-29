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
package com.simsilica.lemur.demo;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.PopUpListPicker;
import com.simsilica.lemur.SelTextField;
import com.simsilica.lemur.state.SelState;
import com.simsilica.lemur.style.BaseStyles;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Janusch Rentenatus
 */
public class SelTextFieldDemo extends SimpleApplication {

    public static String[] LEMUR = new String[]{"AbstractCursorEvent",
        "AbstractEffect",
        "AbstractGuiComponent",
        "AbstractGuiControlListener",
        "AbstractNodeControl",
        "AbstractTween",
        "AnalogFunctionListener",
        "Animation",
        "AnimationState",
        "Attributes",
        "Axis",
        "Axis",
        "BaseAppState",
        "BasePickState",
        "BaseStyles",
        "BasicDemo",
        "BorderLayout",
        "BorderLayout.Position",
        "BoxLayout",
        "Button",
        "Button",
        "Button.ButtonAction",
        "CameraTweens",
        "Checkbox",
        "Checkbox.ToggleCommand",
        "CheckboxModel",
        "ColoredComponent",
        "Command",
        "CommandMap",
        "ComponentStack",
        "ConsumingMouseListener",
        "Container",
        "ContainsSelector",
        "CursorButtonEvent",
        "CursorEventControl",
        "CursorListener",
        "CursorMotionEvent",
        "DefaultCheckboxModel",
        "DefaultCursorListener",
        "DefaultDocumentModel",
        "DefaultFocusTraversalControl",
        "DefaultMouseListener",
        "DefaultRangedValueModel",
        "DefaultRawInputListener",
        "Deformation",
        "Deformations",
        "Deformations.Cylindrical",
        "Deformations.Ramp",
        "DMesh",
        "DocumentModel",
        "DocumentModelFilter",
        "DragHandler",
        "DynamicInsetsComponent",
        "Effect",
        "EffectControl",
        "EffectInfo",
        "ElementId",
        "ElementSelector",
        "FillMode",
        "FocusChangeEvent",
        "FocusChangeListener",
        "FocusManagerState",
        "FocusMouseListener",
        "FocusNavigationFunctions",
        "FocusNavigationState",
        "FocusTarget",
        "FocusTraversal",
        "FocusTraversal.TraversalDirection",
        "FocusTraversalAdapter",
        "FunctionId",
        "GuiComponent",
        "GuiControl",
        "GuiControlListener",
        "GuiGlobals",
        "GuiLayout",
        "GuiMaterial",
        "GuiUpdateListener",
        "HAlignment",
        "HoverMouseListener",
        "IconComponent",
        "InputConfigListener",
        "InputDevice",
        "InputDevice.DeviceAxis",
        "InputDevice.DeviceButton",
        "InputMapper",
        "InputMapper.Mapping",
        "InputState",
        "Insets3f",
        "InsetsComponent",
        "KeyAction",
        "KeyActionListener",
        "KeyInterceptState",
        "KeyListener",
        "KeyModifiers",
        "Label",
        "LayerComparator",
        "LightingMaterialAdapter",
        "MBox",
        "MethodCommand",
        "ModifiedKeyInputEvent",
        "MouseAppState",
        "MouseEventControl",
        "MouseListener",
        "Panel",
        "PanelTweens",
        "PasswordField",
        "PickEventSession",
        "PickEventSession.RootEntry",
        "PickState",
        "PopupState",
        "PopupState.ClickMode",
        "ProgressBar",
        "QuadBackgroundComponent",
        "RangedValueModel",
        "RollupPanel",
        "Selector",
        "Slider",
        "SpatialTweens",
        "SpringGridLayout",
        "StateFunctionListener",
        "StateMethodDelegate",
        "StyleAttribute",
        "StyleDebugMouseListener",
        "StyleDefaults",
        "StyleLoader",
        "Styles",
        "StyleTree",
        "TabbedPanel",
        "TbtQuad",
        "TbtQuadBackgroundComponent",
        "TextComponent",
        "TextEntryComponent",
        "TextField",
        "TextFilters",
        "TouchAppState",
        "Tween",
        "TweenAnimation",
        "Tweens",
        "UnshadedMaterialAdapter",
        "VAlignment",
        "VersionedHolder",
        "VersionedObject",
        "VersionedReference",};

    public static void main(String[] args) {
        SelTextFieldDemo app = new SelTextFieldDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        setPauseOnLostFocus(false);

        // Initialize the globals access so that the defualt
        // components can find what they need.
        GuiGlobals.initialize(this);

        // Load the 'glass' style
        BaseStyles.loadGlassStyle();

        // Set 'glass' as the default style when not specified
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        //GuiGlobals.getInstance().getStyles().setDefaultStyle("root");

        flyCam.setEnabled(false);

        SelState selState = new SelState(this, getViewPort(), rootNode);
        stateManager.attach(selState);

        Node t1 = testUI(selState, new ColorRGBA(0.6f, 0f, 0f, 1f), new ColorRGBA(0f, 0.4f, 0.3f, 1f));
        rootNode.attachChild(t1);
        t1.setLocalTranslation(-12, 0, -400);
        t1.rotate(0f, -1f, 0.8f);
        t1.scale(1.5f);

        Node t2 = testUI(selState, new ColorRGBA(0f, 0.6f, 0f, 1f));
        rootNode.attachChild(t2);
        t2.setLocalTranslation(-12, 0, -400);
        t2.rotate(0f, 1f, 3.8f);
        t2.scale(0.8f);

        Node t3 = testUI(selState, new ColorRGBA(0.2f, 0.4f, 0.7f, 1f), new ColorRGBA(0.8f, 0.3f, 0.2f, 1f));
        rootNode.attachChild(t3);
        t3.setLocalTranslation(-12, 0, -401);
        t1.scale(1.2f);

        Node t4 = button();
        rootNode.attachChild(t4);
        t4.setLocalTranslation(-40, 0, -100);
        t4.scale(1.5f);
    }

    public Node button() {
        Button popButton = new Button("v");

        popButton.setMaxWidth(18f);
        popButton.setTextHAlignment(HAlignment.Center);

        List<String> items
                = Arrays.asList(LEMUR);

        PopUpListPicker plp = PopUpListPicker.onButton(this, popButton, items, 1f);
        plp.setColor(ColorRGBA.White);
        plp.setSelectColor(new ColorRGBA(0.2f, 0.2f, 0.6f, 1f), new ColorRGBA(0.2f, 0.2f, 0.6f, 1f), new ColorRGBA(0.3f, 0.1f, 0.4f, 1f), new ColorRGBA(0.3f, 0.1f, 0.4f, 1f));

        return popButton;
    }

    public Node testUI(SelState selState, ColorRGBA col) {
        SelTextField tf = new SelTextField("1234567890");
        selState.addControlTo(tf);
        tf.setSelectColor(col);
        return testUI(tf);
    }

    public Node testUI(SelState selState, ColorRGBA colA, ColorRGBA colB) {
        SelTextField tf = new SelTextField("1234567890");
        selState.addControlTo(tf);
        tf.setSelectColor(colA, colA, colB, colB);
        return testUI(tf);
    }

    public Node testUI(SelTextField tf) {
        // Create a simple container for our elements
        Container myWindow = new Container();

        // Add some elements
        Label aLabel = myWindow.addChild(new Label("Use popup menu too."));
        myWindow.addChild(aLabel);
        myWindow.addChild(tf);
        return myWindow;
    }

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

}
