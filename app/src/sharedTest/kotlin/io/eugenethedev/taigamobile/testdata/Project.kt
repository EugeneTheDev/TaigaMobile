package io.eugenethedev.taigamobile.testdata

abstract class Project(
    val name: String,
    val description: String,
    val creator: User,
    val team: List<User>,
    val defaultRoleName: String
) {
    /**
     * Test data for content inside project. Note, that you have to name every task DIFFERENT.
     * Data is placed here in the same order in which it will be initialized
     */
    open val epics: List<Epic> = emptyList()
    open val sprints: List<Sprint> = emptyList()
    open val userstories: List<UserStory> = emptyList()
    open val issues: List<Issue> = emptyList()
}