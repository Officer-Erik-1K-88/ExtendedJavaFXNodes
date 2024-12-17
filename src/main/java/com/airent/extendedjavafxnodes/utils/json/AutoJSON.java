package com.airent.extendedjavafxnodes.utils.json;

import org.json.JSONObject;

public interface AutoJSON {
    default JSONObject identifierJSON() {
        return new JSONObject();
    }
}
