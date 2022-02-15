/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simsilica.lemur.demo;

import com.jme3.app.SimpleApplication;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

/**
 *
 * @author Aufricer
 * @author Janusch Rentenatus
 */
public class TextEntryDemo extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        // Initialize the globals access so that the defualt
        // components can find what they need.
        GuiGlobals.initialize(this);

        // Load the 'glass' style
        BaseStyles.loadGlassStyle();

        // Set 'glass' as the default style when not specified
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        //GuiGlobals.getInstance().getStyles().setDefaultStyle("root");

        TextEntryDemoState demoState = new TextEntryDemoState();
        stateManager.attach(demoState);
    }

    public static void main(String[] args) {
        TextEntryDemo app = new TextEntryDemo();
        app.start();
    }

}
