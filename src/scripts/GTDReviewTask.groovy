import freeplaneGTD.DoneMover
import freeplaneGTD.GTDMapReader
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.Proxy
import groovy.transform.*

// @ExecutionModes({on_single_node="main_menu_scripting/freeplaneGTD[addons.archiveTask]"})
/*
=========================================================
 Freeplane GTD+

 Copyright (c)2016 Gergely Papp

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

=========================================================
*/

def reviewTask = new DoneMover()
def (reviewWeekNode, reviewNode, archiveNode) = ["freeplaneGTD.config.reviewWeekDirName", "freeplaneGTD.config.reviewDirName", "freeplaneGTD.config.archiveDirName"].stream().map({
    reviewTask.findOrCreateTargetDir(node, TextUtils.getText(it))
}).collect()
reviewTask.execute(reviewNode, node, {node -> node != archiveNode && node != reviewWeekNode})

