/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 2012-2014, Purdue University
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates
 *
 * All rights reserved.
 */
package com.oracle.truffle.r.test.builtins;

import org.junit.*;

import com.oracle.truffle.r.test.*;

// Checkstyle: stop line length check
public class TestBuiltin_cos extends TestBase {

    @Test
    public void testcos1() {
        assertEval(Ignored.Unknown,
                        "argv <- list(c(-6.28318530717959, -6.1261056745001, -5.96902604182061, -5.81194640914112, -5.65486677646163, -5.49778714378214, -5.34070751110265, -5.18362787842316, -5.02654824574367, -4.86946861306418, -4.71238898038469, -4.5553093477052, -4.39822971502571, -4.24115008234622, -4.08407044966673, -3.92699081698724, -3.76991118430775, -3.61283155162826, -3.45575191894877, -3.29867228626928, -3.14159265358979, -2.9845130209103, -2.82743338823081, -2.67035375555132, -2.51327412287183, -2.35619449019234, -2.19911485751286, -2.04203522483337, -1.88495559215388, -1.72787595947439, -1.5707963267949, -1.41371669411541, -1.25663706143592, -1.09955742875643, -0.942477796076938, -0.785398163397448, -0.628318530717959, -0.471238898038469, -0.314159265358979, -0.15707963267949, 0, 0.15707963267949, 0.314159265358979, 0.471238898038469, 0.628318530717959, 0.785398163397448, 0.942477796076938, 1.09955742875643, 1.25663706143592, 1.41371669411541, 1.5707963267949, 1.72787595947439, 1.88495559215388, 2.04203522483337, 2.19911485751286, 2.35619449019234, 2.51327412287183, 2.67035375555133, 2.82743338823081, 2.9845130209103, 3.14159265358979, 3.29867228626928, 3.45575191894877, 3.61283155162826, 3.76991118430775, 3.92699081698724, 4.08407044966673, 4.24115008234622, 4.39822971502571, 4.5553093477052, 4.71238898038469, 4.86946861306418, 5.02654824574367, 5.18362787842316, 5.34070751110265, 5.49778714378214, 5.65486677646163, 5.81194640914112, 5.96902604182061, 6.1261056745001, 6.28318530717959, 6.44026493985908, 6.59734457253857, 6.75442420521805, 6.91150383789754, 7.06858347057704, 7.22566310325652, 7.38274273593601, 7.5398223686155, 7.69690200129499, 7.85398163397448, 8.01106126665397, 8.16814089933346, 8.32522053201295, 8.48230016469244, 8.63937979737193, 8.79645943005142, 8.95353906273091, 9.1106186954104, 9.26769832808989, 9.42477796076938));cos(argv[[1]]);");
    }

    @Test
    public void testcos2() {
        assertEval(Ignored.Unknown,
                        "argv <- list(c(0-3i, 0-2.96984924623116i, 0-2.93969849246231i, 0-2.90954773869347i, 0-2.87939698492462i, 0-2.84924623115578i, 0-2.81909547738693i, 0-2.78894472361809i, 0-2.75879396984925i, 0-2.7286432160804i, 0-2.69849246231156i, 0-2.66834170854271i, 0-2.63819095477387i, 0-2.60804020100502i, 0-2.57788944723618i, 0-2.54773869346734i, 0-2.51758793969849i, 0-2.48743718592965i, 0-2.4572864321608i, 0-2.42713567839196i, 0-2.39698492462312i, 0-2.36683417085427i, 0-2.33668341708543i, 0-2.30653266331658i, 0-2.27638190954774i, 0-2.24623115577889i, 0-2.21608040201005i, 0-2.18592964824121i, 0-2.15577889447236i, 0-2.12562814070352i, 0-2.09547738693467i, 0-2.06532663316583i, 0-2.03517587939699i, 0-2.00502512562814i, 0-1.9748743718593i, 0-1.94472361809045i, 0-1.91457286432161i, 0-1.88442211055276i, 0-1.85427135678392i, 0-1.82412060301508i, 0-1.79396984924623i, 0-1.76381909547739i, 0-1.73366834170854i, 0-1.7035175879397i, 0-1.67336683417085i, 0-1.64321608040201i, 0-1.61306532663317i, 0-1.58291457286432i, 0-1.55276381909548i, 0-1.52261306532663i, 0-1.49246231155779i, 0-1.46231155778894i, 0-1.4321608040201i, 0-1.40201005025126i, 0-1.37185929648241i, 0-1.34170854271357i, 0-1.31155778894472i, 0-1.28140703517588i, 0-1.25125628140704i, 0-1.22110552763819i, 0-1.19095477386935i, 0-1.1608040201005i, 0-1.13065326633166i, 0-1.10050251256281i, 0-1.07035175879397i, 0-1.04020100502513i, 0-1.01005025125628i, 0-0.979899497487437i, 0-0.949748743718593i, 0-0.919597989949749i, 0-0.889447236180905i, 0-0.859296482412061i, 0-0.829145728643216i, 0-0.798994974874372i, 0-0.768844221105528i, 0-0.738693467336684i, 0-0.70854271356784i, 0-0.678391959798995i, 0-0.648241206030151i, 0-0.618090452261307i, 0-0.587939698492463i, 0-0.557788944723618i, 0-0.527638190954774i, 0-0.49748743718593i, 0-0.467336683417086i, 0-0.437185929648241i, 0-0.407035175879397i, 0-0.376884422110553i, 0-0.346733668341709i, 0-0.316582914572864i, 0-0.28643216080402i, 0-0.256281407035176i, 0-0.226130653266332i, 0-0.195979899497488i, 0-0.165829145728643i, 0-0.135678391959799i, 0-0.105527638190955i, 0-0.0753768844221105i, 0-0.0452261306532664i, 0-0.0150753768844223i, 0+0.0150753768844218i, 0+0.0452261306532664i, 0+0.0753768844221105i, 0+0.105527638190955i, 0+0.135678391959799i, 0+0.165829145728643i, 0+0.195979899497488i, 0+0.226130653266332i, 0+0.256281407035176i, 0+0.28643216080402i, 0+0.316582914572864i, 0+0.346733668341709i, 0+0.376884422110553i, 0+0.407035175879397i, 0+0.437185929648241i, 0+0.467336683417085i, 0+0.49748743718593i, 0+0.527638190954774i, 0+0.557788944723618i, 0+0.587939698492462i, 0+0.618090452261306i, 0+0.648241206030151i, 0+0.678391959798995i, 0+0.708542713567839i, 0+0.738693467336683i, 0+0.768844221105527i, 0+0.798994974874372i, 0+0.829145728643216i, 0+0.85929648241206i, 0+0.889447236180904i, 0+0.919597989949748i, 0+0.949748743718593i, 0+0.979899497487437i, 0+1.01005025125628i, 0+1.04020100502513i, 0+1.07035175879397i, 0+1.10050251256281i, 0+1.13065326633166i, 0+1.1608040201005i, 0+1.19095477386935i, 0+1.22110552763819i, 0+1.25125628140704i, 0+1.28140703517588i, 0+1.31155778894472i, 0+1.34170854271357i, 0+1.37185929648241i, 0+1.40201005025126i, 0+1.4321608040201i, 0+1.46231155778894i, 0+1.49246231155779i, 0+1.52261306532663i, 0+1.55276381909548i, 0+1.58291457286432i, 0+1.61306532663317i, 0+1.64321608040201i, 0+1.67336683417085i, 0+1.7035175879397i, 0+1.73366834170854i, 0+1.76381909547739i, 0+1.79396984924623i, 0+1.82412060301507i, 0+1.85427135678392i, 0+1.88442211055276i, 0+1.91457286432161i, 0+1.94472361809045i, 0+1.9748743718593i, 0+2.00502512562814i, 0+2.03517587939698i, 0+2.06532663316583i, 0+2.09547738693467i, 0+2.12562814070352i, 0+2.15577889447236i, 0+2.18592964824121i, 0+2.21608040201005i, 0+2.24623115577889i, 0+2.27638190954774i, 0+2.30653266331658i, 0+2.33668341708543i, 0+2.36683417085427i, 0+2.39698492462312i, 0+2.42713567839196i, 0+2.4572864321608i, 0+2.48743718592965i, 0+2.51758793969849i, 0+2.54773869346734i, 0+2.57788944723618i, 0+2.60804020100502i, 0+2.63819095477387i, 0+2.66834170854271i, 0+2.69849246231156i, 0+2.7286432160804i, 0+2.75879396984925i, 0+2.78894472361809i, 0+2.81909547738693i, 0+2.84924623115578i, 0+2.87939698492462i, 0+2.90954773869347i, 0+2.93969849246231i, 0+2.96984924623116i, 0+3i));cos(argv[[1]]);");
    }

    @Test
    public void testcos3() {
        assertEval("argv <- list(structure(c(2, 3, 4, 5, 6, 2, 5, 10, 17, 26, 5, 15, 31, 53, 81), .Dim = c(5L, 3L)));cos(argv[[1]]);");
    }

    @Test
    public void testcos4() {
        assertEval(Ignored.Unknown, "argv <- list(Inf);cos(argv[[1]]);");
    }

    @Test
    public void testcos5() {
        assertEval(Ignored.Unknown,
                        "argv <- list(c(3.14159265358979, 6.28318530717959, 1.5707963267949, 3.14159265358979, 4.71238898038469, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 0.785398163397448, 1.5707963267949, 2.35619449019234, 3.14159265358979, 3.92699081698724, 4.71238898038469, 5.49778714378214, 6.28318530717959, 2.0943951023932, 4.18879020478639, 6.28318530717959, 3.14159265358979, 6.28318530717959, 0.897597901025655, 1.79519580205131, 2.69279370307697, 3.59039160410262, 4.48798950512828, 5.38558740615393, 6.28318530717959, 2.0943951023932, 4.18879020478639, 6.28318530717959, 1.25663706143592, 2.51327412287183, 3.76991118430775, 5.02654824574367, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 2.0943951023932, 4.18879020478639, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 1.25663706143592, 2.51327412287183, 3.76991118430775, 5.02654824574367, 6.28318530717959, 3.14159265358979, 6.28318530717959, 2.0943951023932, 4.18879020478639, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959, 3.14159265358979, 6.28318530717959));cos(argv[[1]]);");
    }

    @Test
    public void testcos6() {
        assertEval(Ignored.Unknown, "argv <- list(logical(0));cos(argv[[1]]);");
    }

    @Test
    public void testTrigExp() {
        assertEval("{ cos(1.2) }");
        assertEval("{ cos(c(0.3,0.6,0.9)) }");
        assertEval(Output.ContainsError, "{ cos() }");
    }
}