package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.GroupQuestionDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.InstrumentDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.QuestionDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.SurveyDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.GroupQuestion;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Instrument;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Survey;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 09/05/2016.
 */
public class SurveyDAOImpl implements SurveyDAO {

    private Context context;

    public SurveyDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean boundPlaceAndInstrument(String placeId, Survey survey) throws SQLException {
            ContentValues[] values = new ContentValues[survey.getInstruments().size()];
            ContentValues value = null;

            for (int i = 0; i < values.length; i++) {
                if (survey.getInstruments().get(i).getGroupQuestions().size() != 0) {
                    value = new ContentValues();
                    value.put(AvBContract.InstrumentPlaceEntry.INSTRUMENT_ID, survey.getInstruments().get(i).getId());
                    value.put(AvBContract.InstrumentPlaceEntry.PLACE_ID, placeId);
                    values[i] = value;
                }
            }

            context.getContentResolver().bulkInsert(
                    AvBContract.InstrumentPlaceEntry.INSTRUMENTPLACE_URI, values);
            return true;
    }

    @Override
    public boolean bulkAddInstrument(InstrumentDAO instrumentDAO,Survey survey) throws SQLException {
        return instrumentDAO.bulkAddInstrument(survey.getInstruments());
    }

    @Override
    public boolean bulkAddQuestionGroup(GroupQuestionDAO groupQuestionDAO, Survey survey) throws SQLException {
        for(Instrument instru : survey.getInstruments()){
            groupQuestionDAO.bulkQuestionGroup(instru.getId(),instru.getGroupQuestions());
        }
        return true;
    }

    @Override
    public boolean bulkAddQuestion(QuestionDAO questionDAO, Survey survey) throws SQLException {
        for(Instrument instru : survey.getInstruments()){
           for(GroupQuestion groupQuestion : instru.getGroupQuestions()){
               questionDAO.bulkInsertQuestion(groupQuestion.getId(),groupQuestion.getQuestions());
           }
        }
        return true;
    }

    @Override
    @Nullable
    public Survey findSurveyByPlaceId(InstrumentDAO instrumentDAO, GroupQuestionDAO groupQuestionDAO, String placeId) throws SQLException {
        Cursor c = context.getContentResolver().query(
                AvBContract.PlaceEntry.getPlaceDetails(placeId), null, null, null, null);

        if (c.moveToNext()) {
            Survey survey = new Survey();
            List<String> listOfInstrumentIds = instrumentDAO.getInstrumentIdListByPlace(placeId);

            List<Instrument> instruments = new ArrayList<Instrument>();

            for (String instrumentId : listOfInstrumentIds) {
                instruments.add(new Instrument(instrumentId, groupQuestionDAO.findGroupByInstrumentId(instrumentId)));
            }

            survey.setInstruments(instruments);

            return survey;
        }else{
            c.close();
            return null;
        }
    }

    private Cursor getPendingSurveyCursor(){
        return  context.getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");
    }

    @Override
    public boolean checkIfThereIsPendingSurvey() {
        Cursor c = getPendingSurveyCursor();

        if(c.moveToNext()){
            c.close();
            return true;
        }else{
            c.close();
            return false;
        }
    }
}
