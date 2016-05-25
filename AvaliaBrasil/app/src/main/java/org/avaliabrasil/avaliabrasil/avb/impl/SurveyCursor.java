package org.avaliabrasil.avaliabrasil.avb.impl;

import android.content.Context;
import android.database.Cursor;

import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.dao.SurveyService;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.GroupQuestion;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Instrument;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Question;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Survey;

/**
 * Created by Developer on 10/05/2016.
 */
public class SurveyCursor implements SurveyService{

    /**
     *
     */
    private int questionCursor = 0;

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

    public SurveyCursor(Context context,Survey survey) throws IllegalArgumentException{
        if(survey == null){
            throw new IllegalArgumentException("Survey can't be null");
        }
        this.survey = survey;
        this.context = context;
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
                    questionCursor = 0;
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
        Cursor c = context.getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");

        c.moveToNext();

        String lastInstrument_id = c.getString(c.getColumnIndex("instrument_id"));
        String lastGroup_id = c.getString(c.getColumnIndex("group_id"));
        String lastQuestion_id = c.getString(c.getColumnIndex("question_id"));

        for (int i = 0; i < survey.getInstruments().size(); i++, instrumentCursor++) {
            if (survey.getInstruments().get(i).getId().contains(lastInstrument_id)) {
                break;
            }
        }

        for (int i = 0; i < survey.getInstruments().get(instrumentCursor).getGroupQuestions().size(); i++, groupCursor++) {
            if (survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(i).getId().contains(lastGroup_id)) {
                break;
            }
        }

        for (int i = 0; i < survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().size(); i++, questionCursor++) {
            if (survey.getInstruments().get(instrumentCursor).getGroupQuestions().get(groupCursor).getQuestions().get(i).getId().contains(lastQuestion_id)) {
                 break;
            }
        }

        c.close();
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
