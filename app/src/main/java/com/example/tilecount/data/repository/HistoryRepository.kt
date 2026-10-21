package com.example.tilecount.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.data.model.TileCountHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class HistoryRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _historyFlow = MutableStateFlow<List<TileCountHistory>>(loadHistory())
    val historyFlow: StateFlow<List<TileCountHistory>> = _historyFlow.asStateFlow()

    fun getHistory(): List<TileCountHistory> = _historyFlow.value

    /**
     * Saves a calculation into history.
     * Checks if it's a consecutive duplicate.
     * Keeps at most MAX_HISTORY_ITEMS (10), removing the oldest when exceeding.
     * Returns true if saved, false if skipped as duplicate.
     */
    fun saveCalculation(history: TileCountHistory): Boolean {
        val currentList = _historyFlow.value.toMutableList()

        // Check if consecutively duplicate with the latest item
        if (currentList.isNotEmpty() && isDuplicate(currentList.first(), history)) {
            return false
        }

        // Add as newest at index 0
        currentList.add(0, history)

        // Limit to 10 entries (remove oldest from end)
        while (currentList.size > MAX_HISTORY_ITEMS) {
            currentList.removeAt(currentList.lastIndex)
        }

        persistHistory(currentList)
        _historyFlow.value = currentList
        return true
    }

    fun deleteItem(id: String) {
        val updated = _historyFlow.value.filterNot { it.id == id }
        persistHistory(updated)
        _historyFlow.value = updated
    }

    fun clearHistory() {
        persistHistory(emptyList())
        _historyFlow.value = emptyList()
    }

    private fun isDuplicate(first: TileCountHistory, second: TileCountHistory): Boolean {
        return first.surfaceType == second.surfaceType &&
                first.surfaceLength == second.surfaceLength &&
                first.surfaceWidth == second.surfaceWidth &&
                first.tileLength == second.tileLength &&
                first.tileWidth == second.tileWidth &&
                first.surfaceLengthUnit == second.surfaceLengthUnit &&
                first.surfaceWidthUnit == second.surfaceWidthUnit &&
                first.tileLengthUnit == second.tileLengthUnit &&
                first.tileWidthUnit == second.tileWidthUnit &&
                first.resultAreaUnit == second.resultAreaUnit &&
                first.wastePercent == second.wastePercent &&
                first.tilesPerBox == second.tilesPerBox
    }

    private fun loadHistory(): List<TileCountHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<TileCountHistory>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    TileCountHistory(
                        id = obj.getString("id"),
                        surfaceType = SurfaceType.valueOf(obj.getString("surfaceType")),
                        surfaceLength = obj.getString("surfaceLength"),
                        surfaceWidth = obj.getString("surfaceWidth"),
                        tileLength = obj.getString("tileLength"),
                        tileWidth = obj.getString("tileWidth"),
                        surfaceLengthUnit = LengthUnit.valueOf(obj.getString("surfaceLengthUnit")),
                        surfaceWidthUnit = LengthUnit.valueOf(obj.getString("surfaceWidthUnit")),
                        tileLengthUnit = LengthUnit.valueOf(obj.getString("tileLengthUnit")),
                        tileWidthUnit = LengthUnit.valueOf(obj.getString("tileWidthUnit")),
                        resultAreaUnit = AreaUnit.valueOf(obj.getString("resultAreaUnit")),
                        wastePercent = obj.getString("wastePercent"),
                        tilesPerBox = if (obj.isNull("tilesPerBox")) null else obj.getString("tilesPerBox"),
                        savedAt = obj.getLong("savedAt")
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    private fun persistHistory(list: List<TileCountHistory>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("surfaceType", item.surfaceType.name)
                put("surfaceLength", item.surfaceLength)
                put("surfaceWidth", item.surfaceWidth)
                put("tileLength", item.tileLength)
                put("tileWidth", item.tileWidth)
                put("surfaceLengthUnit", item.surfaceLengthUnit.name)
                put("surfaceWidthUnit", item.surfaceWidthUnit.name)
                put("tileLengthUnit", item.tileLengthUnit.name)
                put("tileWidthUnit", item.tileWidthUnit.name)
                put("resultAreaUnit", item.resultAreaUnit.name)
                put("wastePercent", item.wastePercent)
                if (item.tilesPerBox != null) {
                    put("tilesPerBox", item.tilesPerBox)
                } else {
                    put("tilesPerBox", JSONObject.NULL)
                }
                put("savedAt", item.savedAt)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "tilecount_history_preferences"
        private const val KEY_HISTORY = "history_json"
        const val MAX_HISTORY_ITEMS = 10
    }
}
