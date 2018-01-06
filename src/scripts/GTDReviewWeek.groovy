import freeplaneGTD.DoneMover
import freeplaneGTD.GTDMapReader
import org.freeplane.core.util.TextUtils
import org.freeplane.plugin.script.proxy.Proxy

// @ExecutionModes({on_single_node="main_menu_scripting/Freeplane GTD/week review"})
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

import freeplaneGTD.DoneMover

def reviewTask = new DoneMover()
Proxy.Node targetNode = reviewTask.findOrCreateTargetDir(node, "Review Week")
Proxy.Node reviewNode = reviewTask.findOrCreateTargetDir(node, TextUtils.getText("freeplaneGTD.config.reviewDirName"))
Proxy.Node archiveNode = reviewTask.findOrCreateTargetDir(node, TextUtils.getText("freeplaneGTD.config.archiveDirName"))
reviewTask.execute(targetNode, reviewNode, {node -> node != archiveNode})

