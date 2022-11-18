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
package de.jare.textfilter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Janusch Rentenatus
 */
public class TextFilter {

    private String filterPattern;
    private boolean patternChanged;
    private List<FPattern> patternList;
    private boolean softEnd;

    public TextFilter() {
        filterPattern = "*";
        softEnd = true;
    }

    public TextFilter(String filterPattern) {
        this.filterPattern = filterPattern;
        this.patternChanged = true;
        softEnd = true;
    }

    public TextFilter(String filterPattern, boolean softEnd) {
        this.filterPattern = filterPattern;
        this.patternChanged = true;
        this.softEnd = softEnd;
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
        this.patternChanged = true;
    }

    public String getFilterPattern() {
        return filterPattern;
    }

    public boolean isSoftEnd() {
        return softEnd;
    }

    public void setSoftEnd(boolean softEnd) {
        this.softEnd = softEnd;
    }

    protected void calculate() {
        patternList = new ArrayList<>();
        char nextModus = '!';
        StringBuilder subText = new StringBuilder();
        for (char c : filterPattern.toCharArray()) {
            if (c == '*' || c == '!') {
                if (!subText.toString().isEmpty()) {
                    patternList.add(new FPattern(nextModus, subText.toString()));
                    subText = new StringBuilder();
                }
                nextModus = c;
            } else if (c == '?') {
                if (!subText.toString().isEmpty()) {
                    patternList.add(new FPattern(nextModus, subText.toString()));
                    subText = new StringBuilder();
                }
                patternList.add(new FPattern(c, "?"));
                nextModus = '!';
            } else if (Character.isUpperCase(c)) {
                if (subText.toString().isEmpty()) {
                    subText.append(c);
                } else {
                    patternList.add(new FPattern(nextModus, subText.toString()));
                    subText = new StringBuilder();
                    nextModus = '*';
                    subText.append(c);
                }
            } else {
                subText.append(c);
            }
        }
        patternList.add(new FPattern(nextModus, subText.toString()));
        patternChanged = false;
    }

    public boolean check(String target) {
        if (patternChanged || patternList == null) {
            calculate();
        }
        int pos = 0;
        for (FPattern fp : patternList) {
            if (fp.getModus() == '?') {
                pos++;
                if (pos > target.length()) {
                    return false;
                }
            } else if (fp.getText().isEmpty()) {
                if (fp.getModus() == '*') {
                    pos = target.length();
                }
            } else {
                int index = target.indexOf(fp.getText(), pos);
                if (index < 0) {
                    return false;
                }
                if (fp.getModus() == '!' && index > pos) {
                    return false;
                }
                pos = index + fp.getText().length();
            }
        }
        return softEnd || (pos == target.length());
    }

    public List<FSelection> render(String target) {
        if (patternChanged || patternList == null) {
            calculate();
        }
        List<FSelection> ret = new ArrayList<>();
        int pos = 0;
        for (FPattern fp : patternList) {
            if (fp.getModus() == '?') {
                ret.add(new FSelection(pos, pos + 1));
                pos++;
            } else if (fp.getText().isEmpty()) {
                if (fp.getModus() == '*') {
                    pos = target.length();
                }
            } else {
                int index = target.indexOf(fp.getText(), pos);
                if (index < 0) {
                    return ret;
                }
                if (fp.getModus() == '!' && index > pos) {
                    return ret;
                }
                pos = index + fp.getText().length();
                ret.add(new FSelection(index, pos));
            }
        }
        return ret;
    }
}
