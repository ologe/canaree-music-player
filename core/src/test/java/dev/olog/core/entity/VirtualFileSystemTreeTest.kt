package dev.olog.core.entity

import org.junit.Assert
import org.junit.Test

class VirtualFileSystemTreeTest {

    @Test
    fun `test root`() {
        val tree = VirtualFileSystemTree(VirtualFileSystemNode(VirtualFileSystemTree.ROOT_PATH))

        Assert.assertEquals(null, tree.root.parent)
        Assert.assertEquals("/", tree.root.value)
        Assert.assertEquals(emptyList<VirtualFileSystemNode>(), tree.root.children)
        val expected = """
            /
            
        """.trimIndent()
        Assert.assertEquals(expected, tree.toString())
    }

    @Test
    fun `test add same path with different quotes, should be added only once`() {
        val tree = VirtualFileSystemTree()
        tree.addPathRecursively("hello")
        tree.addPathRecursively("/hello")
        tree.addPathRecursively("hello/")
        tree.addPathRecursively("/hello/")

        val expected = """
            /
             hello
            
        """.trimIndent()
        Assert.assertEquals(expected, tree.toString())
    }

    @Test
    fun `test add complex paths`() {
        val tree = VirtualFileSystemTree()
        tree.addPathRecursively("/")
        tree.addPathRecursively("Music")
        tree.addPathRecursively("Podcasts/")
        tree.addPathRecursively("Music/Notifications")
        tree.addPathRecursively("Music/Ringtone")
        tree.addPathRecursively("Podcasts/Artist")
        tree.addPathRecursively("Music/Ringtone/Random")

        val expected = """
            /
             Music
              Notifications
              Ringtone
               Random
             Podcasts
              Artist
            
        """.trimIndent()
        Assert.assertEquals(expected, tree.toString())
    }

    @Test
    fun `test findNode and path`() {
        val tree = VirtualFileSystemTree()
        tree.addPathRecursively("/")
        tree.addPathRecursively("Music")
        tree.addPathRecursively("Podcasts/")
        tree.addPathRecursively("Music/Notifications")
        tree.addPathRecursively("Music/Ringtone")
        tree.addPathRecursively("Podcasts/Artist")
        tree.addPathRecursively("Music/Ringtone/Random")

        Assert.assertEquals("/", tree.findNode(VirtualFileSystemTree.ROOT_PATH)!!.path())
        Assert.assertEquals("Music/", tree.findNode("Music")!!.path())
        Assert.assertEquals("Podcasts/", tree.findNode("Podcasts")!!.path())
        Assert.assertEquals("Music/Ringtone/", tree.findNode("Music/Ringtone")!!.path())
        Assert.assertEquals("Music/Ringtone/Random/", tree.findNode("Music/Ringtone/Random")!!.path())
//
        Assert.assertEquals(null, tree.findChildren(""))
        Assert.assertEquals(null, tree.findChildren("invalid"))
    }

    @Test
    fun `test findChildren`() {
        val tree = VirtualFileSystemTree()
        tree.addPathRecursively("/")
        tree.addPathRecursively("Music")
        tree.addPathRecursively("Podcasts/")
        tree.addPathRecursively("Music/Notifications")
        tree.addPathRecursively("Music/Ringtone")
        tree.addPathRecursively("Podcasts/Artist")
        tree.addPathRecursively("Music/Ringtone/Random")

        Assert.assertEquals(listOf("Music/", "Podcasts/"), tree.findChildren("/")!!.map { it.path() })
        Assert.assertEquals(listOf("Music/Notifications/", "Music/Ringtone/"), tree.findChildren("Music")!!.map { it.path() })
        Assert.assertEquals(listOf("Podcasts/Artist/"), tree.findChildren("Podcasts")!!.map { it.path() })

        Assert.assertEquals(null, tree.findChildren(""))
        Assert.assertEquals(null, tree.findChildren("invalid"))
    }

}