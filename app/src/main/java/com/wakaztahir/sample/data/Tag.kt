package com.wakaztahir.mindnode.data

import com.wakaztahir.sample.data.AppDatabase
import java.util.*

data class Tag(
    val id: Long? = null,
    val cloudId : String? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val title: String,
    val createdDate: Long = System.currentTimeMillis(),
    val editedDate: Long = createdDate,
    val deleted: Boolean = false
)

fun AppDatabase.getAllTags(): List<Tag> {
    return db.tagQueries.getAllTags(::tagMapper).executeAsList()
}

suspend fun AppDatabase.getUndeletedTags(): List<Tag> {
    return db.tagQueries.getUndeletedTags(::tagMapper).executeAsList()
}

fun AppDatabase.getTagByUUID(uuid: String): Tag? {
    return db.tagQueries.getTag(uuid = uuid, mapper = ::tagMapper).executeAsOneOrNull()
}

fun AppDatabase.getTagsByUUIDs(uuid: List<String>): List<Tag> {
    return db.tagQueries.getTags(uuid = uuid, mapper = ::tagMapper).executeAsList()
}

fun AppDatabase.updateTag(tag: Tag) {
    db.tagQueries.updateTag(
        cloudId = tag.cloudId,
        title = tag.title,
        deleted = tag.deleted,
        createdDate = tag.createdDate,
        editedDate = tag.editedDate,
        uuid = tag.uuid
    )
}

fun AppDatabase.updateTagTitle(uid: String, title: String) {
    db.tagQueries.updateTagTitle(title = title, editedDate = System.currentTimeMillis(), uuid = uid)
}


fun AppDatabase.deleteTag(tag: Tag) {
    if (tag.cloudId == null) {
        db.tagQueries.deleteTag(tag.uuid)
    } else {
        db.tagQueries.removeTag(editedDate = System.currentTimeMillis(), uuid = tag.uuid)
    }
}

fun AppDatabase.deleteTag(uuid: String) = db.tagQueries.deleteTag(uuid = uuid)

fun AppDatabase.removeTags(tags: Collection<Tag>) {
    db.tagQueries.removeTags(
        uuid = tags.map { it.uuid },
        editedDate = System.currentTimeMillis()
    )
}

fun AppDatabase.updateTagCloudId(uuid: String, cloudId: String) {
    db.tagQueries.updateTagCloudId(cloudId = cloudId, uuid = uuid)
}

fun AppDatabase.insertTag(tag: Tag) = db.tagQueries.insertTag(
    cloudId = tag.cloudId,
    uuid = tag.uuid,
    title = tag.title,
    deleted = tag.deleted,
    createdDate = tag.createdDate,
    editedDate = tag.editedDate
)

fun tagMapper(
    id: Long,
    cloudId: String?,
    uuid: String,
    title: String,
    deleted: Boolean,
    createdDate: Long,
    editedDate: Long,
) = Tag(
    id = id,
    cloudId = cloudId,
    uuid = uuid,
    title = title,
    deleted = deleted,
    createdDate = createdDate,
    editedDate = editedDate
)