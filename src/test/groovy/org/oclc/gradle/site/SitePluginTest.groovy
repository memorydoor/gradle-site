package org.oclc.gradle.site

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
class SitePluginTest extends Specification{

    private final Project project = ProjectBuilder.builder().build()
    private final SitePlugin plugin = new SitePlugin()

    def "Applying the Site plugin does add 'site' task"() {
        when:
        plugin.apply(project)
        then:
        project.tasks.size() == 2
        project.tasks.getByName("site") != null
    }

//    def "the 'site' task is a type of SiteTask.class"() {
//        when:
//        plugin.apply(project)
//        then:
//        DefaultTask.class instanceOf(project.tasks.getByName("site").)
//    }
}
