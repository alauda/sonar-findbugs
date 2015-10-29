/*
 * Findbugs :: IT :: Plugin
 * Copyright (C) 2014 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.it.java.suite;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.MavenBuild;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class FBContribTest {

  @ClassRule
  public static Orchestrator orchestrator = FindbugsTestSuite.ORCHESTRATOR;

  private static final String PROJECT_KEY = "org.sonar.tests:fb-contrib";

  @Before
  public void deleteData() {
    orchestrator.resetData();
  }

  private static String keyFor(String s) {
    return PROJECT_KEY + ":src/" + s + ".java";
  }

  @Test
  public void test() throws Exception {
    MavenBuild build = MavenBuild.create()
      .setPom(FindbugsTestSuite.projectPom("simple"))
      .setProperty("sonar.profile", "IT")
      .setProperty("sonar.dynamicAnalysis", "false")
      .setProperty("sonar.findbugs.confidenceLevel", "low")
      .setCleanPackageSonarGoals();
    orchestrator.executeBuild(build);

    Sonar wsClient = orchestrator.getServer().getWsClient();
    assertThat(wsClient.find(new ResourceQuery(PROJECT_KEY)).getVersion()).isEqualTo("0.1-SNAPSHOT");
    assertThat(getProjectMeasure("violations").getIntValue()).isEqualTo(3);
    IssueClient issueClient = orchestrator.getServer().wsClient().issueClient();
    List<Issue> issues = issueClient.find(IssueQuery.create()).list();
    assertThat(issues).hasSize(3);
    issues = issueClient.find(IssueQuery.create().components(keyFor("Simple")).rules("fb-contrib:IPU_IMPROPER_PROPERTIES_USE_SETPROPERTY")).list();
    // SONARJAVA-216:
    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).message()).isEqualTo("Method Simple.method() uses Properties.put instead of Properties.setProperty");

    issues = issueClient.find(IssueQuery.create().components(keyFor("Simple")).rules("findsecbugs:PREDICTABLE_RANDOM")).list();
    assertThat(issues).hasSize(1);
    assertThat(issues.get(0).message()).isEqualTo("Use of java.util.Random is predictable.");
  }

  protected Measure getProjectMeasure(String metricKey) {
    return orchestrator.getServer().getWsClient().find(ResourceQuery.createForMetrics(PROJECT_KEY, metricKey)).getMeasure(metricKey);
  }

}