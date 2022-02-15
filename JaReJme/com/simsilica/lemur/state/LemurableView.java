/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simsilica.lemur.state;

import com.jme3.app.Application;
import com.jme3.renderer.ViewPort;
import com.simsilica.lemur.event.BasePickState;

/**
 *
 * @author Janusch Rentenatus
 */
public interface LemurableView {

    public default void addCollisionRoot(Application app, ViewPort viewWithLemurComponents) {
        BasePickState bps = app.getStateManager().getState(BasePickState.class);
        if (bps != null) {
            bps.addCollisionRoot(viewWithLemurComponents, viewWithLemurComponents.getName() + "View");
        }
    }

    public default void removeCollisionRoot(Application app, ViewPort viewWithLemurComponents) {
        BasePickState bps = app.getStateManager().getState(BasePickState.class);
        if (bps != null) {
            bps.removeCollisionRoot(viewWithLemurComponents);
        }
    }

}
