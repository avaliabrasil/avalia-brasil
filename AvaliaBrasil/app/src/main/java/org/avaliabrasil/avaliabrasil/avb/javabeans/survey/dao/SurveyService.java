package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.GroupQuestion;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Instrument;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Question;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Survey;

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
