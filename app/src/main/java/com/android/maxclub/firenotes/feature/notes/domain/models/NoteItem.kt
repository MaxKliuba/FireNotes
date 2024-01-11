package com.android.maxclub.firenotes.feature.notes.domain.models

sealed class NoteItem(
    val content: String,
    val position: Long,
    val id: String,
) {
    class Text(content: String, position: Long, id: String = "") : NoteItem(content, position, id)

    class ToDo(
        val checked: Boolean,
        content: String,
        position: Long,
        id: String = "",
    ) : NoteItem(content, position, id) {
        override fun toContentString(): String =
            "${if (checked) "[*]" else "[_]"}\t${super.toContentString()}"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as ToDo

            return checked == other.checked
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + checked.hashCode()
            return result
        }
    }

    open fun toContentString(): String = content

    fun copy(
        content: String = this.content,
        position: Long = this.position,
        id: String = this.id,
    ): NoteItem =
        when (this) {
            is Text -> Text(content, position, id)
            is ToDo -> ToDo(checked, content, position, id)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteItem

        if (content != other.content) return false
        if (position != other.position) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}