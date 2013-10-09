package org.oclc.gradle.site

import org.apache.maven.doxia.site.decoration.Skin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
class SitePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("site", SitePluginExtension)

        project.tasks.create("site", SiteTask.class)
        project.tasks.create("site-deploy", SiteDeployTask.class, {task -> task.dependsOn("site")})
        project.tasks.create("site-clean", SiteCleanTask.class)

        project.afterEvaluate { p ->
            def siteTask = project.tasks.findByName("site")



            if (p.site.skin.artifactId != null && p.site.skin.groupId != null && p.site.skin.version != null) {
                Skin skin = new Skin()
                skin.setArtifactId(p.site.skin.artifactId)
                skin.setGroupId(p.site.skin.groupId)
                skin.setVersion(p.site.skin.version)

                siteTask.skin = skin

                println "1:" +  skin
            }
            println "2:"

            def siteDeploy = project.tasks.findByName("site-deploy")
            siteDeploy.url = p.site.url

            if (p.site.outputDirectory != null) {
                def siteOutputDirectory = project.file(p.site.outputDirectory)
                siteDeploy.inputDirectory = siteOutputDirectory

                def siteTasks = p.tasks.findAll { it.name == 'site' || it.name == 'site-clean' }
                siteTasks.each { task ->
                    task.outputDirectory = siteOutputDirectory
                }
            }
        }
    }
}
