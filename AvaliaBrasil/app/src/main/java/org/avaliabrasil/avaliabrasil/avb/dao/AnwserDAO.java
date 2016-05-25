package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Anwser;

import java.util.List;

/**
 * Created by Developer on 12/05/2016.
 */
public interface AnwserDAO {

    public void insertAnwser(Anwser anwser);
    public void deleteAllAnwserByPlaceId(String placeId);
    public List<Anwser> getUnsendedAnwsers();
    public void setSurveyAsCompleted();
    public void deleteSendedSurvey();

}
