/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.test.functions.builtin.part1;

import org.apache.sysds.runtime.matrix.data.MatrixValue.CellIndex;
import org.apache.sysds.runtime.meta.MatrixCharacteristics;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class BuiltinStepLMEvolutionaryTest extends AutomatedTestBase {
	private static final String TEST_NAME = "steplmevolutionary";
	private static final String TEST_DIR = "functions/builtin/";
	private static final String TEST_CLASS_DIR =
			TEST_DIR + BuiltinStepLMEvolutionaryTest.class.getSimpleName() + "/";

	@Override
	public void setUp() {
		addTestConfiguration(TEST_NAME,
				new TestConfiguration(TEST_CLASS_DIR, TEST_NAME, new String[]{"B", "S"}));
	}

	@Test
	public void testStepLMEvolutionary() {
		runStepLMEvolutionary(10, new double[]{1, 2, 5, 6, 8, 11});
	}

	@Test
	public void testStepLMEvolutionarySingleFeature() {
		runStepLMEvolutionary(1, new double[]{1, 2});
	}

	private void runStepLMEvolutionary(int cols, double[] expected) {
		loadTestConfiguration(getTestConfiguration(TEST_NAME));
		fullDMLScriptName = SCRIPT_DIR + TEST_DIR + TEST_NAME + ".dml";

		programArgs = new String[]{
				"-args", input("X"), input("y"), output("B"), output("S")
		};

		int rows = 100;
		double[][] X = getRandomMatrix(rows, cols, 0, 1, 0.8, 42);
		double[][] y = new double[rows][1];

		for (int i = 0; i < rows; i++) {
			y[i][0] = 3 * X[i][0] + 0.1 * (i % 7);
		}

		writeInputMatrixWithMTD("X", X, true);
		writeInputMatrixWithMTD("y", y, true);
		runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

		MatrixCharacteristics coefficients = readDMLMetaDataFile("B");
		Assert.assertEquals(cols + 1, coefficients.getRows());
		Assert.assertEquals(1, coefficients.getCols());

		MatrixCharacteristics selection = readDMLMetaDataFile("S");
		Assert.assertEquals(1, selection.getRows());
		Assert.assertEquals(expected.length, selection.getCols());

		var selected = readDMLMatrixFromOutputDir("S");
		for (int i = 0; i < expected.length; i++) {
			double actual = selected.getOrDefault(new CellIndex(1, i + 1), Double.NaN);
			Assert.assertEquals("S[1, " + (i + 1) + "]", expected[i], actual, 0.0);
		}
	}
}
