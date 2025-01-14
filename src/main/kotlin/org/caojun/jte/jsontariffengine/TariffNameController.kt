package org.caojun.jte.jsontariffengine

class TariffNameController {

    private var initTariffName = ""
    private var resultTariffName = ""
    fun setInitialData(data: String) {
        initTariffName = data
    }

    fun getReturnData(): Pair<String, String> {
        return Pair(initTariffName, resultTariffName)
    }
}