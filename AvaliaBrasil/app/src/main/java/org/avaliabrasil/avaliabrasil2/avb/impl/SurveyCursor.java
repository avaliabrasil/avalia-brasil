package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.avaliabrasil.avaliabrasil2.avb.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.SurveyService;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Anwser;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.GroupQuestion;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Instrument;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Question;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;

/**
 * Created by Developer on 10/05/2016.
 */
public class SurveyCursor implements SurveyService{

    /**
     *
     */
    private int questionCursor = -1;

    /**
     *
     */
    private int instrumentCursor = 0;

    /**
     *
     */
    private int groupCursor = 0;

    /**
     *
     */
    private Survey survey;

    /**
     *
     */
    private Context context;

    private AnwserDAO anwserDAO;

    public SurveyCursor(Context context,Survey survey , AnwserDAO anwserDAO) throws IllegalArgumentException{
        if(survey == null){
            throw new IllegalArgumentException("Survey can't be null");
        }
        this.survey = survey;
        this.context = context;
        this.anwserDAO = anwserDAO;
    }

    @Override
    public Survey getSurvey() {
        return survey;
    }

    @Override
    public Question getNextQuestion(){
        if (survey.getInstruments().size() > instrumentCursor) {
            if (survey.getInstruments().get(instrumentCursor).getGroupQuestions().size() > groupCursor) {
                if (survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().size() > (questionCursor+1)) {
                    questionCursor++;
                    return survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().get(questionCursor);
                } else {
                    questionCursor = -1;
                    groupCursor++;
                    return getNextQuestion();
                }
            } else {
                questionCursor = 0;
                groupCursor = 0;
                instrumentCursor++;
                return getNextQuestion();
            }
        } else {
            return null;
        }
    }

    @Override
    public void preparePendingSurvey(){
        Anwser lastAnwser = anwserDAO.getLastAnwserBySurveyId(survey.getSurveyId());

        if(lastAnwser != null){

            for (int i = 0; i < survey.getInstruments().size(); i++, instrumentCursor++) {
                if (survey.getInstruments().get(i).getId().contains(lastAnwser.getInstrumentId())) {
                    break;
                }
            }

            for (int i = 0; i < survey.getInstruments().get(instrumentCursor).getGroupQuestions().size(); i++, groupCursor++) {
                if (survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(i).getId().contains(lastAnwser.getGroupId())) {
                    break;
                }
            }

            for (int i = 0; i < survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().size(); i++, questionCursor++) {
                if (survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().get(i).getId().contains(lastAnwser.getQuestion_id())) {
                    ++questionCursor;
                    break;
                }
            }
        }
    }

    @Override
    public Instrument peekInstrument() {
        return survey.getInstruments().get(instrumentCursor);
    }

    @Override
    public GroupQuestion peekGroup() {
        return survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor);
    }

    @Override
    public Question peekQuestion() {
        return survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().get(questionCursor);
    }
}
