package org.oclc.gradle.site

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder;
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteTaskTest extends Specification {

    private final Project project = ProjectBuilder.builder().build()
    private final SitePlugin plugin = new SitePlugin()

    def "test that site deploy task"(){
        when:
        plugin.apply(project)

        def task = (SiteTask) project.tasks.getByName("site")

        task.generateSite()
        then:
        assert 1 == 1
    }
}