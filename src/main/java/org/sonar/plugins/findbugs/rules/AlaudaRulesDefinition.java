/*
 * SonarQube Findbugs Plugin
 * Copyright (C) 2012 SonarSource
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
package org.sonar.plugins.findbugs.rules;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.plugins.findbugs.FindbugsRulesDefinitionXmlLoader;
import org.sonar.plugins.java.Java;

public class AlaudaRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = "java";
  public static final String REPOSITORY_NAME = "SonarAnalyzer";

  @Override
  public void define(Context context) {
    NewRepository repository = context
            .createRepository(REPOSITORY_KEY, Java.KEY)
            .setName(REPOSITORY_NAME);

    FindbugsRulesDefinitionXmlLoader ruleLoader = new FindbugsRulesDefinitionXmlLoader();
    ruleLoader.load(repository, AlaudaRulesDefinition.class.getResourceAsStream("/org/sonar/plugins/findbugs/rules-sonar.xml"), "UTF-8");
    repository.done();
  }

}
