package io.eugenethedev.taigamobile.testdata

import java.time.LocalDate

object TestData {
    object User {
        const val username = "test"
        const val password = "testing_password"
        const val email = "test@test.com"
        const val fullName = "Test Test"
    }

    object Project {
        const val name = "Cool Test Project"
        const val description = "And cool description"

        /**
         * Test data for content inside project. Note, that you have to name every task DIFFERENT.
         * Data is placed here in the same order in which it will be initialized
         */

        val epics = listOf(
            Epic(
                name = "Epic #1",
                description = "Epic 1",
                comments = listOf(
                    "Important",
                    "Yes"
                ),
                isAssigned = true,
                isClosed = true
            ),
            Epic(
                name = "Epic #2",
                isWatching = true
            )
        )

        val sprints = listOf(
            Sprint(
                name = "Sprint 1",
                start = LocalDate.of(2021, 9, 1),
                end = LocalDate.of(2021, 9, 15)
            ),

            Sprint(
                name = "Sprint 2",
                start = LocalDate.of(2021, 9, 16),
                end = LocalDate.of(2021, 9, 30),
                tasks = listOf(
                    Task("Storyless task 1", "Desc"),
                    Task("Storyless task 2")
                )
            )
        )

        val userstories = listOf(
            UserStory(
                name = "Cool user story",
                description = "Very interesting description",
                epics = epics,
                comments = listOf(
                    "Comment"
                )
            ),
            UserStory(
                name = "Another user story",
                description = "Description",
                isAssigned = true,
                isClosed = true,
                sprint = sprints[0],
                tasks = listOf(
                    Task(
                        name = "Subtask 1",
                        isAssigned = true,
                        isClosed = true
                    ),
                    Task(
                        name = "Subtask 2",
                        isClosed = true
                    )
                )
            ),
            UserStory(
                name = "Great story",
                description = "",
                isWatching = true,
                sprint = sprints[1],
                tasks = listOf(
                    Task("Task #42", isWatching = true),
                    Task("Task #24")
                )
            )
        )

        val issues = listOf(
            Issue(
                name = "Issue #1",
                description = "Problem #1",
                isAssigned = true,
                isClosed = true,
                sprint = sprints[0]
            ),
            Issue(
                name = "Issue #2",
                description = "Problem #2",
                sprint = sprints[1]
            ),
            Issue(
                name = "Issue #3",
                description = "Problem #3"
            )
        )

    }
}
