/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2017  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.cpa.arg.witnessexport;

import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.ConfigurationBuilder;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.io.IO;
import org.sosy_lab.common.io.TempFile;
import org.sosy_lab.cpachecker.util.automaton.AutomatonGraphmlCommon.WitnessType;
import org.sosy_lab.cpachecker.util.test.CPATestRunner;
import org.sosy_lab.cpachecker.util.test.CPATestRunner.ExpectedVerdict;
import org.sosy_lab.cpachecker.util.test.TestDataTools;
import org.sosy_lab.cpachecker.util.test.TestResults;

public class WitnessExporterTest {

  private static final Pattern PROOF_WITNESS_OPTION_PATTERN =
      Pattern.compile("(cpa.arg.proofWitness\\s*=\\s*)(.+)");

  private enum WitnessGenerationConfig {
    K_INDUCTION("kInduction"),

    BDD_CONCURRENCY("bddAnalysis-concurrency"),

    PREDICATE_ANALYSIS("predicateAnalysis");

    private final String fileName;

    private WitnessGenerationConfig(String pConfigName) {
      fileName = String.format("witnessGeneration-%s.properties", pConfigName);
    }
  }

  private static final String SPECIFICATION_OPTION = "specification";

  private static final String TEST_DIR_PATH = "test/programs/witnessValidation/";

  @Test(timeout = 90000)
  public void multivar_true() throws Exception {
    new WitnessTester("multivar.i", ExpectedVerdict.TRUE, WitnessGenerationConfig.K_INDUCTION)
        .performTest();
  }

  @Test(timeout = 90000)
  public void minepump_spec1_product33_false() throws Exception {
    new WitnessTester(
            "minepump_spec1_product33.cil.c",
            ExpectedVerdict.FALSE,
            WitnessGenerationConfig.PREDICATE_ANALYSIS)
        .performTest();
  }

  @Test(timeout = 90000)
  public void minepump_spec1_product33_false_2() throws Exception {
    new WitnessTester(
            "minepump_spec1_product33.cil.c",
            ExpectedVerdict.FALSE,
            WitnessGenerationConfig.K_INDUCTION)
        .performTest();
  }

  @Test(timeout = 90000)
  public void concurrency_false_fib_bench() throws Exception {
    new WitnessTester(
            "fib_bench-2.i", ExpectedVerdict.FALSE, WitnessGenerationConfig.BDD_CONCURRENCY)
        .performTest();
  }

  @Test(timeout = 90000)
  public void concurrency_false_mix000_power() throws Exception {
    new WitnessTester(
            "mix000_power.oepc.i", ExpectedVerdict.FALSE, WitnessGenerationConfig.BDD_CONCURRENCY)
        .performTest();
  }

  @Test(timeout = 90000)
  public void rule60_list2_false() throws Exception {
    new WitnessTester(
            "rule60_list2.i", ExpectedVerdict.FALSE, WitnessGenerationConfig.PREDICATE_ANALYSIS)
        .performTest();
  }

  private static void performTest(
      String pFilename,
      String pSpecification,
      ExpectedVerdict pExpected,
      WitnessGenerationConfig pGenerationConfig,
      Map<String, String> pOverrideOptions)
      throws Exception {
    String fullPath = Paths.get(TEST_DIR_PATH, pFilename).toString();

    TempCompressedFilePath witnessPath = new TempCompressedFilePath("witness", ".graphml");

    WitnessType witnessType =
        generateWitness(
            fullPath, pExpected, pGenerationConfig, pSpecification, pOverrideOptions, witnessPath);

    validateWitness(
        fullPath, pSpecification, pExpected, pOverrideOptions, witnessPath, witnessType);
  }

  private static WitnessType generateWitness(
      String pFilePath,
      ExpectedVerdict pExpected,
      WitnessGenerationConfig pGenerationConfig,
      String pSpecification,
      Map<String, String> pOverrideOptions,
      TempCompressedFilePath pWitnessPath)
      throws Exception {
    Map<String, String> overrideOptions = new LinkedHashMap<>(pOverrideOptions);
    overrideOptions.put(
        "counterexample.export.graphml", pWitnessPath.uncompressedFilePath.toString());
    if (pGenerationConfig.equals(WitnessGenerationConfig.K_INDUCTION)) {
      overrideOptions.put("bmc.invariantsExport", pWitnessPath.uncompressedFilePath.toString());
      overrideOptions.put(
          "parallelAlgorithm.configFiles",
          "config/components/kInduction/kInduction.properties, "
              + getInvGenFile(pWitnessPath)
              + "::supply-reached-refinable");
    } else {
      overrideOptions.put("cpa.arg.proofWitness", pWitnessPath.uncompressedFilePath.toString());
    }
    Configuration generationConfig =
        getProperties(pGenerationConfig.fileName, overrideOptions, pSpecification);

    TestResults results = CPATestRunner.run(generationConfig, pFilePath);
    // Trigger statistics so that the witness is written to the file
    results.getCheckerResult().writeOutputFiles();

    switch (pExpected) {
      case TRUE:
        results.assertIsSafe();
        return WitnessType.CORRECTNESS_WITNESS;
      case FALSE:
        results.assertIsUnsafe();
        return WitnessType.VIOLATION_WITNESS;
      default:
        assertWithMessage("Cannot determine expected result.").fail();
        throw new AssertionError("Unreachable code.");
    }
  }

  private static String getInvGenFile(TempCompressedFilePath pWitnessPath) throws IOException {
    Path origInvGenConfigFile = Paths.get("test/config/invariantGeneration-witness.properties");
    Path invGenConfigFile =
        origInvGenConfigFile.resolveSibling(
            pWitnessPath.uncompressedFilePath.getFileName() + ".properties");
    invGenConfigFile.toFile().deleteOnExit();
    Files.copy(origInvGenConfigFile, invGenConfigFile);
    List<String> lines;
    try (BufferedReader reader = Files.newBufferedReader(invGenConfigFile)) {
      lines =
          reader
              .lines()
              .map(
                  line -> {
                    Matcher matcher = PROOF_WITNESS_OPTION_PATTERN.matcher(line);
                    if (matcher.matches()) {
                      return matcher.group(1) + pWitnessPath.uncompressedFilePath.toString();
                    }
                    return line;
                  })
              .collect(Collectors.toList());
    }
    try (Writer writer = IO.openOutputFile(invGenConfigFile, Charsets.UTF_8)) {
      for (String line : lines) {
        writer.write(line);
        writer.write(System.lineSeparator());
      }
    }
    return invGenConfigFile.toString();
  }

  private static void validateWitness(
      String pFilePath,
      String pSpecification,
      ExpectedVerdict pExpected,
      Map<String, String> pOverrideOptions,
      TempCompressedFilePath witnessPath,
      WitnessType witnessType)
      throws Exception {
    Map<String, String> overrideOptions = new LinkedHashMap<>(pOverrideOptions);
    final String validationConfigFile;
    String specification = pSpecification;
    switch (witnessType) {
      case CORRECTNESS_WITNESS:
        validationConfigFile = "correctnessWitnessValidation.properties";
        overrideOptions.put(
            "invariantGeneration.kInduction.invariantsAutomatonFile",
            witnessPath.uncompressedFilePath.toString());
        break;
      case VIOLATION_WITNESS:
        validationConfigFile = "violationWitnessValidation.properties";
        specification =
            Joiner.on(',').join(specification, witnessPath.compressedFilePath.toString());
        break;
      default:
        throw new AssertionError("Unsupported witness type " + witnessType);
    }
    Configuration validationConfig =
        getProperties(validationConfigFile, overrideOptions, specification);

    TestResults results = CPATestRunner.run(validationConfig, pFilePath);

    switch (pExpected) {
      case TRUE:
        results.assertIsSafe();
        break;
      case FALSE:
        results.assertIsUnsafe();
        break;
      default:
        assertWithMessage("Cannot determine expected result.").fail();
    }
  }

  private static Configuration getProperties(
      String pConfigFile, Map<String, String> pOverrideOptions, String pSpecification)
      throws InvalidConfigurationException {
    ConfigurationBuilder configBuilder =
        TestDataTools.configurationForTest()
            .loadFromResource(WitnessExporterTest.class, pConfigFile);
    if (!Strings.isNullOrEmpty(pSpecification)) {
      pOverrideOptions.put(SPECIFICATION_OPTION, pSpecification);
    }
    return configBuilder.setOptions(pOverrideOptions).build();
  }

  private static class TempCompressedFilePath {

    private final Path uncompressedFilePath;

    private final Path compressedFilePath;

    public TempCompressedFilePath(String pPrefix, String pSuffix) throws IOException {
      String compressedSuffix = ".gz";
      compressedFilePath =
          TempFile.builder()
              .prefix(pPrefix)
              .suffix(pSuffix + compressedSuffix)
              .create()
              .toAbsolutePath();
      Path compressedFileNamePath = compressedFilePath.getFileName();
      if (compressedFileNamePath == null) {
        throw new AssertionError("Files obtained from TempFile.builder().create() should always have a file name.");
      }
      String fileName = compressedFileNamePath.toString();
      String uncompressedFileName =
          fileName.substring(0, fileName.length() - compressedSuffix.length());
      uncompressedFilePath = compressedFilePath.resolveSibling(uncompressedFileName);
      uncompressedFilePath.toFile().deleteOnExit();
    }

    @Override
    public String toString() {
      return compressedFilePath.toString();
    }

    @Override
    public boolean equals(Object pOther) {
      if (this == pOther) {
        return true;
      }
      if (pOther instanceof TempCompressedFilePath) {
        return compressedFilePath.equals(((TempCompressedFilePath) pOther).compressedFilePath);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return compressedFilePath.hashCode();
    }
  }

  private static class WitnessTester {

    private final String programFile;
    private final ExpectedVerdict expected;
    private final WitnessGenerationConfig generationConfig;

    private String specificationFile = "config/specification/default.spc";

    private ImmutableMap.Builder<String, String> overrideOptionsBuilder = ImmutableMap.builder();

    private WitnessTester(
        String pProgramFile, ExpectedVerdict pExpected, WitnessGenerationConfig pGenerationConfig) {
      programFile = Objects.requireNonNull(pProgramFile);
      expected = pExpected;
      generationConfig = Objects.requireNonNull(pGenerationConfig);
    }

    @CanIgnoreReturnValue
    public WitnessTester forSpecification(String pSpecificationFile) {
      specificationFile = Objects.requireNonNull(pSpecificationFile);
      return this;
    }

    @CanIgnoreReturnValue
    public WitnessTester addOverrideOption(String pOptionName, String pOptionValue) {
      overrideOptionsBuilder.put(pOptionName, pOptionValue);
      return this;
    }

    public void performTest() throws Exception {
      WitnessExporterTest.performTest(
          programFile,
          specificationFile,
          expected,
          generationConfig,
          overrideOptionsBuilder.build());
    }
  }
}
