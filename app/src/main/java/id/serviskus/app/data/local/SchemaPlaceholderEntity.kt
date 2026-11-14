package id.serviskus.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Minimal placeholder entity required so Room compiles version 1 and exports its schema.
 * Will be removed without a migration before the first release when domain entities are added.
 */
@Entity(tableName = "schema_placeholder")
data class SchemaPlaceholderEntity(
    @PrimaryKey val id: Long = 1L,
)
