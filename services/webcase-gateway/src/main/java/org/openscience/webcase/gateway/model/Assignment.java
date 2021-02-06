/*
 * The MIT License
 *
 * Copyright 2018 Michael Wenk [https://github.com/michaelwenk].
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.openscience.webcase.gateway.model;


import lombok.*;

/**
 * @author Michael Wenk [https://github.com/michaelwenk]
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Assignment {

    private int[][] assignments;

    private int getAssignmentsCount() {
        if (this.assignments.length > 0) {
            return this.assignments[0].length;
        }
        return 0;
    }

    private int getSetAssignmentsCount(final int dim) {
        int setAssignmentsCounter = 0;
        if (this.containsDim(dim)) {
            for (int j = 0; j < this.assignments[dim].length; j++) {
                if (this.assignments[dim][j] != -1) {
                    setAssignmentsCounter++;
                }
            }
        }
        return setAssignmentsCounter;
    }

    public Boolean isFullyAssigned(final int dim) {
        if (!this.containsDim(dim)) {
            return null;
        }

        return this.getSetAssignmentsCount(dim) == this.getAssignmentsCount();
    }

    private boolean containsDim(final int dim) {
        return dim >= 0 && dim < this.assignments.length;
    }
}