package com.xzkj.test.capture

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject


class JsonInit {

    /**
     * 对象转json
     */
    fun <T> dataToJson(data: T): String {
        try {
            return JSONObject.toJSONString(data)
        } catch (e: Exception) {
            e.message
        }
        return ""
    }
    /**
     * 数组转json
     */
    fun arrayToJson(obj: Any?): String? {
        try {
            return JSONArray.toJSONString(obj)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * json转对象
     */
    fun <T> jsonToData(str: String?, cls: Class<T>?): T? {
        try {
            if (str != null && "" != str.trim { it <= ' ' }) {
                return JSONArray.parseObject(str.trim { it <= ' ' }, cls)
            }
        } catch (e: java.lang.Exception) {
            e.message
        }
        return null
    }


}