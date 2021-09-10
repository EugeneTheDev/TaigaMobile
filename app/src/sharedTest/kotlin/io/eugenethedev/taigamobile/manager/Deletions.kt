package io.eugenethedev.taigamobile.manager

interface Deletions {
    fun TaigaTestInstanceManager.clearTables() = tx {
        getAllProdTables().forEach {
            createStatement().execute("alter table $it disable trigger all")
            createStatement().execute("delete from $it where true")
            createStatement().execute("alter table $it enable trigger all")
        }
    }
}
