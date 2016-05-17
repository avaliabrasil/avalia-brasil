package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.AnwserService;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Anwser;

import java.util.List;

/**
 * Created by Developer on 13/05/2016.
 */
public class AnwserServiceImpl implements AnwserService{

    public AnwserServiceImpl() {
    }

    @Override
    public JsonArray prepareForSendAnwser(AnwserDAO anwserDAO) {
        JsonArray anwserArray = new JsonArray();

        List<Anwser> anwsers = anwserDAO.getUnsendedAnwsers();

        for(Anwser awn : anwsers){
            JsonObject obj = new JsonObject();

            obj.addProperty("questionId", awn.getQuestion_id());
            obj.addProperty("questionType", !awn.getNumber().isEmpty() ? "number" : !awn.getComment().isEmpty() ? "comment" : "likert");
            obj.addProperty("answer", !awn.getNumber().isEmpty() ? awn.getNumber() : !awn.getComment().isEmpty() ? awn.getComment() : awn.getLikert());

            anwserArray.add(obj);
        }
        
        return anwserArray;
    }
}
