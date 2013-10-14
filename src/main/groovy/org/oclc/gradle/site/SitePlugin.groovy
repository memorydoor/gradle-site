package org.oclc.gradle.site

import org.apache.maven.doxia.site.decoration.Skin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
class SitePlugin implements Plugin<Project> {

    private static final String TASK_NAME_SITE_DEPLOY = "site-deploy"
    private static final String TASK_NAME_SITE = "site"
    private static final String TASK_NAME_SITE_CLEAN = "site-clean"
    private static final String TASK_GROUP_NAME = "Site"

    @Override
    void apply(Project project) {

        project.extensions.create(TASK_NAME_SITE, SitePluginExtension)

        def assignGroup = { task -> task.group = TASK_GROUP_NAME }
        project.tasks.create(TASK_NAME_SITE, SiteTask.class, assignGroup)
        project.tasks.create(TASK_NAME_SITE_DEPLOY, SiteDeployTask.class, assignGroup)
        project.tasks.create(TASK_NAME_SITE_CLEAN, SiteCleanTask.class, assignGroup)

        def siteTask = project.tasks.findByName(TASK_NAME_SITE)
        def siteDeployTask = project.tasks.findByName(TASK_NAME_SITE_DEPLOY)
        def siteCleanTask = project.tasks.findByName(TASK_NAME_SITE_CLEAN)

        siteTask.description = "Generates site."
        siteDeployTask.description = "Deploys the output of site task to 'site.url'."
        siteCleanTask.description = "Cleans the output of site task."

        project.afterEvaluate { p ->

            if (p.site.skin.artifactId != null && p.site.skin.groupId != null && p.site.skin.version != null) {
                Skin skin = new Skin()
                skin.setArtifactId(p.site.skin.artifactId)
                skin.setGroupId(p.site.skin.groupId)
                skin.setVersion(p.site.skin.version)

                siteTask.skin = skin
            }

            siteDeployTask.url = p.site.url

            if (p.site.outputDirectory != null) {
                def siteOutputDirectory = project.file(p.site.outputDirectory)

                siteDeployTask.inputDirectory = siteOutputDirectory

                siteTask.outputDirectory = siteOutputDirectory
                siteCleanTask.outputDirectory = siteOutputDirectory
            }

            //rules for executing tasks
            siteTask.onlyIf { siteTask.siteDirectory.exists() }

            dependsOnIfExist("javadoc", project, siteTask)
            dependsOnIfExist("test", project, siteTask)
            dependsOnIfExist("pmdMain", project, siteTask)
            dependsOnIfExist("pmdTest", project, siteTask)
            dependsOnIfExist("findbugsMain", project, siteTask)
            dependsOnIfExist("findbugsTest", project, siteTask)

            siteDeployTask.dependsOn(TASK_NAME_SITE)
            siteDeployTask.onlyIf { siteDeployTask.inputDirectory.exists() }
        }
    }

    private void dependsOnIfExist(String taskName, def project, def siteTask) {
        def javaDocTask = project.tasks.findByName(taskName)
        if (javaDocTask != null) {
            siteTask.dependsOn(javaDocTask)
        }
    }
}
