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
		loadTestConfiguration(getTestConfiguration(TEST_NAME));
		fullDMLScriptName = SCRIPT_DIR + TEST_DIR + TEST_NAME + ".dml";

		programArgs = new String[]{
				"-args", input("X"), input("y"), output("B"), output("S")
		};

		int rows = 100;
		int cols = 10;
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
	}
}
