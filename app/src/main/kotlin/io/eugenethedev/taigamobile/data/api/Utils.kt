package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.CommonTaskType

/**
 * Since API endpoints for different types of tasks are often the same (only part in the path is different),
 * here are some value classes to simplify interactions with API
 */

// plural form
@JvmInline
value class CommonTaskPathPlural private constructor(val path: String) {
    constructor(commonTaskType: CommonTaskType): this(
        when (commonTaskType) {
            CommonTaskType.UserStory -> "userstories"
            CommonTaskType.Task -> "tasks"
            CommonTaskType.Epic -> "epics"
            CommonTaskType.Issue -> "issues"
        }
    )
}

// singular form
@JvmInline
value class CommonTaskPathSingular private constructor(val path: String) {
    constructor(commonTaskType: CommonTaskType): this(
        when (commonTaskType) {
            CommonTaskType.UserStory -> "userstory"
            CommonTaskType.Task -> "task"
            CommonTaskType.Epic -> "epic"
            CommonTaskType.Issue -> "issue"
        }
    )
}
