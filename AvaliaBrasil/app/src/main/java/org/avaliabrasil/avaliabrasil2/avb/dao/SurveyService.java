package org.avaliabrasil.avaliabrasil2.avb.dao;

import com.google.gson.JsonArray;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.GroupQuestion;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Instrument;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Question;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;

/**
 * Created by Developer on 10/05/2016.
 */
public interface SurveyService {

    public Question getNextQuestion();

    public void preparePendingSurvey();

    public Survey getSurvey();

    public Instrument peekInstrument();

    public GroupQuestion peekGroup();

    public Question peekQuestion();
}
