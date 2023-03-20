package dev.olog.core.entity

import android.provider.MediaStore
import java.io.File

/**
 * Meant for [MediaStore.Audio.AudioColumns.RELATIVE_PATH], all paths must end with '/'
 *
 * path examples:
 *  [ROOT_PATH]
 *  Music/
 *  Music/SubFolder/
 */
data class VirtualFileSystemTree(
    val root: VirtualFileSystemNode = VirtualFileSystemNode(value = ROOT_PATH, parent = null)
) {

    companion object {
        const val ROOT_PATH = "/"
    }

    fun addPathRecursively(path: String) {
        if (path == ROOT_PATH) {
            return
        }
        val elements = path.trim(File.separatorChar).split(File.separator)
        root.addPathRecursively(elements)
    }

    fun findNode(path: String): VirtualFileSystemNode? {
        if (path == root.value) {
            return root
        }

        val elements = path.trim(File.separatorChar).split(File.separator)

        var child = root

        for (element in elements) {
            for (node in child.children) {
                if (node.value == element) {
                    child = node
                    break
                }
            }
        }

        if (child == root) {
            return null
        }
        return child
    }

    fun findChildren(path: String): List<VirtualFileSystemNode>? {
        if (path == root.value) {
            return root.children
        }

        val elements = path.trim(File.separatorChar).split(File.separator)

        var child = root

        for (element in elements) {
            for (node in child.children) {
                if (node.value == element) {
                    child = node
                    break
                }
            }
        }

        if (child == root) {
            return null
        }
        return child.children
    }

    override fun toString(): String {
        return buildString {
            traversePreOrder(root, 0) { node, index ->
                appendLine(" ".repeat(index) + node.toString())
            }
        }
    }

    private fun traversePreOrder(node: VirtualFileSystemNode, depth: Int, action: (VirtualFileSystemNode, Int) -> Unit) {
        action(node, depth)

        for (child in node.children) {
            traversePreOrder(child, depth + 1, action)
        }
    }

}

data class VirtualFileSystemNode(
    val value: String,
    val parent: VirtualFileSystemNode? = null,
    private val _children: MutableList<VirtualFileSystemNode> = mutableListOf(),
) {

    val children: List<VirtualFileSystemNode>
        get() = _children.sortedBy { it.value.lowercase() }

    val isRoot: Boolean
        get() = parent == null

    fun addPathRecursively(elements: List<String>) {
        if (elements.isEmpty()) {
            return
        }
        val childValue = elements.first()
        val node = _children.find { it.value == childValue }
            ?: VirtualFileSystemNode(childValue, this).also { _children.add(it) }
        node.addPathRecursively(elements.drop(1))
    }

    override fun toString(): String = value

    fun path(): String {
        if (parent == null) {
            return value
        }
        return buildString {
            var node: VirtualFileSystemNode? = this@VirtualFileSystemNode
            while (node?.parent != null) {
                insert(0, node.value + "/")
                node = node.parent
            }
        }
    }

}