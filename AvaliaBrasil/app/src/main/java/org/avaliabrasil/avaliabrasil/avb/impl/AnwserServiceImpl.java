package org.avaliabrasil.avaliabrasil.avb.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.avb.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.AnwserService;
import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.dao.NewPlaceDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.PlaceDetailsDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.ResultDetails;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Anwser;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.NewPlace;

import java.util.List;

/**
 * Created by Developer on 13/05/2016.
 */
public class AnwserServiceImpl implements AnwserService{

    private NewPlaceDAO newPlaceDAO;
    private AnwserDAO anwserDAO;
    private PlaceDetailsDAO placeDetailsDAO;

    public AnwserServiceImpl(NewPlaceDAO newPlaceDAO, AnwserDAO anwserDAO, PlaceDetailsDAO placeDetailsDAO) {
        this.newPlaceDAO = newPlaceDAO;
        this.anwserDAO = anwserDAO;
        this.placeDetailsDAO = placeDetailsDAO;
    }

    @Override
    public JsonObject prepareForSendAnwser(String userId,String placeId) {
        JsonObject response = new JsonObject();

        response.addProperty("userId", "1");

        checkIfNewPlace(placeId, response);

        response.add("answers", checkForAnwsers());

        return response;
    }

    private JsonArray checkForAnwsers() {
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

    private void checkIfNewPlace(String placeId, JsonObject response) {
        NewPlace newPlace = newPlaceDAO.findNewPlaceByPlaceId(placeId);

        if (newPlace != null) {
            response.addProperty("newPlace", true);
            response.addProperty("placeTypeId", newPlace.getPlaceType());
            ResultDetails details = placeDetailsDAO.getPlaceDetailsByPlaceId(placeId);

            if(details != null){
                response.addProperty("address", details.getVicinity());
                response.addProperty("name", details.getName());
                response.addProperty("cityName", details.getCity());
                response.addProperty("stateLetter", details.getStateLetter());
            }
        } else {
            response.addProperty("newPlace", false);
        }
    }
}
