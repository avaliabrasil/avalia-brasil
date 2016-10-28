package org.avaliabrasil.avaliabrasil2.avb.dao;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Anwser;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;

import java.util.List;

/**
 * Created by Developer on 12/05/2016.
 */
public interface AnwserDAO {

    public void insertAnwser(Anwser anwser);
    public List<Anwser> getUnsendedAnwsersBySurveyId(String surveyId);
    public void setSurveyAsCompleted();
    public void deleteSendedSurvey();
    public Anwser getLastAnwserBySurveyId(String surveyId);
    public void deleteAnswerBySurveyId(String surveyId);

}
