/*-
 * -\-\-
 * DBeam Core
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.dbeam.jobs;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.Stats;
import com.spotify.dbeam.beam.MetricsHelper;
import com.spotify.dbeam.options.OutputOptions;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

/** Used on e2e test, allows benchmarking with different configuration parameters. */
public class BenchJdbcAvroJob {

  public interface BenchJdbcAvroOptions extends PipelineOptions {
    @Description("The JDBC connection url to perform the export.")
    @Default.Integer(3)
    int getExecutions();

    void setExecutions(int value);
  }

  private final PipelineOptions pipelineOptions;
  private List<Map<String, Long>> metrics = newArrayList();

  public BenchJdbcAvroJob(final PipelineOptions pipelineOptions) {
    this.pipelineOptions = pipelineOptions;
  }

  public static BenchJdbcAvroJob create(final String[] cmdLineArgs) {
    PipelineOptionsFactory.register(BenchJdbcAvroOptions.class);
    PipelineOptions options = JdbcAvroJob.buildPipelineOptions(cmdLineArgs);
    return new BenchJdbcAvroJob(options);
  }

  public void run() throws Exception {
    int executions = pipelineOptions.as(BenchJdbcAvroOptions.class).getExecutions();
    for (int i = 0; i < executions; i++) {
      String output =
          String.format("%s/run_%d", pipelineOptions.as(OutputOptions.class).getOutput(), i);
      final PipelineResult pipelineResult = JdbcAvroJob.create(pipelineOptions, output).runExport();
      this.metrics.add(MetricsHelper.getMetrics(pipelineResult));
    }
    System.out.println("Summary for BenchJdbcAvroJob");
    System.out.println(pipelineOptions.toString());
    System.out.println(tsvMetrics());
  }

  private String tsvMetrics() {
    final List<String> columns =
        newArrayList(
            "recordCount", "writeElapsedMs", "msPerMillionRows", "bytesWritten", "KbWritePerSec");
    final Collector<CharSequence, ?, String> tabJoining = Collectors.joining("\t");
    final Stream<String> lines =
        IntStream.range(0, this.metrics.size())
            .mapToObj(
                i ->
                    String.format(
                        "run_%02d  \t%s",
                        i,
                        columns.stream()
                            .map(
                                c ->
                                    String.format(
                                        "% 10d",
                                        Optional.of(this.metrics.get(i).get(c)).orElse(0L)))
                            .collect(tabJoining)));
    final List<Stats> stats =
        columns.stream()
            .map(
                c ->
                    Stats.of(
                        (Iterable<Long>)
                            this.metrics.stream().map(m -> Optional.of(m.get(c)).orElse(0L))
                                ::iterator))
            .collect(Collectors.toList());
    final Map<String, Function<Stats, Double>> relevantStats =
        ImmutableMap.of(
            "max     ", Stats::max,
            "mean    ", Stats::mean,
            "min     ", Stats::min,
            "stddev  ", Stats::populationStandardDeviation);
    final Stream<String> statsSummary =
        relevantStats.entrySet().stream()
            .map(
                e ->
                    String.format(
                        "%s\t%s",
                        e.getKey(),
                        stats.stream()
                            .map(e.getValue())
                            .map(v -> String.format("% 10.1f", v))
                            .collect(tabJoining)));
    return String.format(
        "name    \t%12s\n%s",
        String.join("\t", columns),
        Stream.concat(lines, statsSummary).collect(Collectors.joining("\n")));
  }

  public static void main(String[] cmdLineArgs) {
    try {
      create(cmdLineArgs).run();
    } catch (Exception e) {
      ExceptionHandling.handleException(e);
    }
  }
}
