package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Anwser;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Survey;

import java.util.List;

/**
 * Created by Developer on 12/05/2016.
 */
public interface AnwserDAO {

    public void insertAnwser(Anwser anwser);
    public void deleteAllAnwserByPlaceId(String placeId);
    public List<Anwser> getUnsendedAnwsersBySurveyId(String surveyId);
    public void setSurveyAsCompleted();
    public void deleteSendedSurvey();
    public Anwser getLastAnwserBySurveyId(String surveyId);

}
