package org.avaliabrasil.avaliabrasil.avb.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Developer on 13/05/2016.
 */
public interface AnwserService {

    public JsonObject prepareForSendAnwser(String userId,String placeId);
}
