package freeplaneGTD

import org.freeplane.plugin.script.proxy.Proxy

/**
 * Created by gpapp on 2016.01.24..
 */
class DoneMover {
    protected final GTDMapReader mapReader = GTDMapReader.instance

    public findOrCreateTargetDir(Proxy.Node node, String targetName) {
        final Proxy.Node rootNode = node.map.root
        final String targetDirName = targetName

        Proxy.Node targetNode = rootNode.children.find {
            it.transformedText==targetDirName
        }
        if(!targetNode) {
            targetNode = rootNode.createChild()
            targetNode.text=targetDirName
        }
        return targetNode
    }

    Proxy.Node findProjectRoot(final Proxy.Node targetDir, final Proxy.Node projectDir) {
        if (projectDir == targetDir.map.root) {
            return targetDir
        }
        Proxy.Node targetParentDir = findProjectRoot(targetDir, projectDir.parent)
        if (projectDir.icons.contains(mapReader.iconProject)) {
            Proxy.Node targetProjectDir = targetParentDir.children.find { it.text == projectDir.text }
            if (!targetProjectDir) {
                targetProjectDir = targetParentDir.appendChild(projectDir);
            }
            return targetProjectDir
        }
        return targetParentDir
    }

    /**
     * Walk up the node structure to find the project path.
     * @param targetDir where to create the new directory
     * @param projectDir which node we currently try to look at
     * @return
     */
    void execute(final Proxy.Node targetDir, final Proxy.Node node, Closure isMovable) {
        // Must reread it every time in case the configuration nodes were changed
        mapReader.findIcons(node.map.root)
        mapReader.internalConvertShorthand(node)

        // don't try to move e.g. Archive to Review
        if (!isMovable(node)) {
            return
        }

        // !! || !v == !(! && v)
        // e.g. it isn't movable itself
        if (!node.icons.contains(mapReader.iconNextAction) || !(node.icons.contains(mapReader.iconCancel) || node.icons.contains(mapReader.iconDone ))) {
            node.children.each {
                if (it==targetDir){
                    return
                }
                this.execute(targetDir, it, isMovable)
            }
            return
        }

        // (! && v) - finish done *task* node
        Proxy.Node targetNode = findProjectRoot(targetDir, node.parent)
        Proxy.Node oldParentPtr = node.parent
        System.out.print("move " + node + " to " + targetNode)

        node.moveTo(targetNode)
        node.left=targetNode.left

        // it was the last child and time to move parent as well && it isn't a map's root
        while (!oldParentPtr.children && oldParentPtr.icons.contains(mapReader.iconProject) && oldParentPtr.parent) {
            Proxy.Node toDelete = oldParentPtr
            oldParentPtr = oldParentPtr.parent
            toDelete.delete()
        }
    }
}
