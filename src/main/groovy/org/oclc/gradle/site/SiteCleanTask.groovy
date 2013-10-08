package org.oclc.gradle.site

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/8/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
class SiteCleanTask extends DefaultTask{

    @Nested
    File outputDir = getProject().file("out/site")

    @TaskAction
    def clean() {
        FileUtils.deleteDirectory(outputDir)
    }

}
