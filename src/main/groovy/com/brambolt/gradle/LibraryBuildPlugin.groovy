/*
 * Copyright 2017-2020 Brambolt ehf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brambolt.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.brambolt.gradle.PluginBuildPlugin.ARTIFACTORY_PLUGIN_ID
import static com.brambolt.gradle.PluginBuildPlugin.BINTRAY_PLUGIN_ID
import static com.brambolt.gradle.PluginBuildPlugin.checkProjectProperties
import static com.brambolt.gradle.PluginBuildPlugin.configureArtifactory
import static com.brambolt.gradle.PluginBuildPlugin.configureBintray
import static com.brambolt.gradle.PluginBuildPlugin.configureDefaultTasks
import static com.brambolt.gradle.PluginBuildPlugin.configureDependencies
import static com.brambolt.gradle.PluginBuildPlugin.configureDerivedPropertiesWithoutPlugins
import static com.brambolt.gradle.PluginBuildPlugin.configureDerivedPropertiesWithPlugins
import static com.brambolt.gradle.PluginBuildPlugin.configureJarTask
import static com.brambolt.gradle.PluginBuildPlugin.configureJavadocJarTask
import static com.brambolt.gradle.PluginBuildPlugin.configureJavaPlugin
import static com.brambolt.gradle.PluginBuildPlugin.configurePluginInclusion
import static com.brambolt.gradle.PluginBuildPlugin.configurePlugins
import static com.brambolt.gradle.PluginBuildPlugin.configureJavaPublishing
import static com.brambolt.gradle.PluginBuildPlugin.configureRepositories
import static com.brambolt.gradle.PluginBuildPlugin.configureSourceJarTask

/**
 * Configures a Gradle build to build and publish a library.
 */
class LibraryBuildPlugin implements Plugin<Project> {

  /**
   * The plugins to apply to the plugin build project.
   */
  List<String> PLUGIN_IDS = [
    'java-library',
    'groovy',
    'maven-publish',
    ARTIFACTORY_PLUGIN_ID,
    BINTRAY_PLUGIN_ID,
    'org.ajoberstar.grgit'
  ]

  /**
   * The project-specific properties that must be set.
   */
  final static List<String> REQUIRED_PROPERTIES = [
    'artifactId',
    'developers',
    'inceptionYear',
    'licenses',
    'release',
    'vcsUrl'
  ]

  /**
   * Maps plugin identifier to project property. If a plugin identifier
   * is included in this map then the plugin will only be applied if the
   * project property is present with a non-empty value that does not
   * evaluate to false.
   */
  Map<String, Closure<Boolean>> pluginInclusion = [:]

  /**
   * Applies the plugin and configures the build.
   * @param project The project to configure
   */
  @Override
  void apply(Project project) {
    project.logger.debug("Applying ${getClass().getCanonicalName()}.")
    configureDerivedPropertiesWithoutPlugins(project)
    configurePluginInclusion(project, pluginInclusion)
    configurePlugins(project, PLUGIN_IDS, pluginInclusion)
    checkProjectProperties(project, REQUIRED_PROPERTIES)
    configureDerivedPropertiesWithPlugins(project)
    logProperties(project)
    configureRepositories(project)
    configureDependencies(project)
    configureJavaPlugin(project)
    configureJarTask(project)
    configureJavadocJarTask(project)
    configureSourceJarTask(project)
    configureJavaPublishing(project)
    configureArtifactory(project)
    configureBintray(project)
    configureDefaultTasks(project)
  }

  /**
   * Logs the required and derived project properties.
   * @param project The project to configure
   */
  void logProperties(Project project) {
    project.logger.info("""
  Artifact id:          ${project.artifactId}
  Branch:               ${project.vcsBranch}
  Commit:               ${project.vcsCommit}
  Description:          ${project.description}
  Group:                ${project.group}
  Name:                 ${project.name}
  VCS URL:              ${project.vcsUrl}
  Version:              ${project.version}
""")
  }
}
