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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiLayout;
import java.util.List;

/**
 * A layout that kids manage and display them on top of each other.
 *
 * @author Paul Speed
 * @author Janusch Rentenatus
 */
public class JoyLayout extends AbstractGuiComponent
        implements GuiLayout, Cloneable {

    private GuiControl parent;

    private List<Node> children = new ArrayList< Node>();

    private Vector3f lastPreferredSize = new Vector3f();

    public JoyLayout() {
    }

    @Override
    public JoyLayout clone() {
        // Easier and better to just instantiate with the proper
        // settings
        JoyLayout result = new JoyLayout();
        return result;
    }

    @Override
    protected void invalidate() {
        if (parent != null) {
            parent.invalidate();
        }
    }

    protected Vector3f getPreferredSize() {
        Vector3f size = Vector3f.ZERO.clone();
        for (Node child : children) {
            size = size.maxLocal(child.getControl(GuiControl.class).getPreferredSize());
        }
        return size;
    }

    @Override
    public void calculatePreferredSize(Vector3f size) {
        Vector3f pref;
        pref = getPreferredSize();
        size.addLocal(pref);
    }

    @Override
    public void reshape(Vector3f pos, Vector3f size) {
        // Note: we use the pos and size for scratch because we
        // are a layout and we should therefore always be last.

        // Make sure the preferred size book-keeping is up to date.
        // Some children don't like being asked to resize without
        // having been asked to calculate their preferred size first.
        calculatePreferredSize(new Vector3f());

        for (Node child : children) {
            child.setLocalTranslation(pos);
            child.getControl(GuiControl.class).setSize(size);
        }

    }

    public <T extends Node> T addChild(T n) {
        if (n.getControl(GuiControl.class) == null) {
            throw new IllegalArgumentException("Child is not GUI element:" + n);
        }
        children.add(n);
        if (parent != null) {
            parent.getNode().attachChild(n);
        }
        invalidate();
        return n;
    }

    public <T extends Node> T addChild(T n, Object... constraints) {
        return addChild(n);
    }

    public void removeChild(Node n) {
        if (!children.remove(n)) {
            throw new RuntimeException("Node is not a child of this layout:" + n);
        }
        if (parent != null) {
            parent.getNode().detachChild(n);
        }
        invalidate();
    }

    public Collection<Node> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public void clearChildren() {
        if (parent != null) {
            // Need to detach any children    
            // Have to make a copy to avoid concurrent mod exceptions
            // now that the containers are smart enough to call remove
            // when detachChild() is called.  A small side-effect.
            // Possibly a better way to do this?  Disable loop-back removal
            // somehow?
            Collection<Node> copy = new ArrayList<Node>(children);
            for (Node n : copy) {
                // Detaching from the parent we know prevents
                // accidentally detaching a node that has been
                // reparented without our knowledge
                parent.getNode().detachChild(n);
            }
        }
        children.clear();
        invalidate();
    }

    @Override
    public void attach(GuiControl parent) {
        this.parent = parent;
        Node self = parent.getNode();
        for (Node child : children) {
            self.attachChild(child);
        }
    }

    @Override
    public void detach(GuiControl parent) {
        this.parent = null;
        // Have to make a copy to avoid concurrent mod exceptions
        // now that the containers are smart enough to call remove
        // when detachChild() is called.  A small side-effect.
        // Possibly a better way to do this?  Disable loop-back removal
        // somehow?
        Collection<Node> copy = new ArrayList<Node>(children);
        for (Node n : copy) {
            n.removeFromParent();
        }
    }
}
