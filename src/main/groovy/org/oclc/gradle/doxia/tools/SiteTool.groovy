package org.oclc.gradle.doxia.tools

import org.apache.maven.doxia.site.decoration.DecorationModel
import org.apache.maven.doxia.site.decoration.Menu
import org.apache.maven.doxia.site.decoration.MenuItem
import org.codehaus.plexus.i18n.I18N
import org.gradle.api.Project

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/8/13
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteTool {

    def I18N i18n

    def populateModulesMenu(Project project, DecorationModel decorationModel) {
        Menu menu = decorationModel.getMenuRef( "modules" );

        if (menu == null) {
            return;
        }

        //menu.name = i18n.getString("site-tool", Locale.default, "decorationModel.menu.projectmodules")
        menu.name = "Modules"

        for (Project subproject : project.subprojects) {
            MenuItem item = new MenuItem()
            item.name = subproject.name
            item.href = "./" + subproject.name + "/index.html"

            menu.addItem(item)
        }

    }
}
