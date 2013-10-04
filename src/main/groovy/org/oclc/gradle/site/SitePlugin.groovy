package org.oclc.gradle.site

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

    @Override
    void apply(Project project) {
        project.tasks.create("site", SiteTask.class)
    }
}
