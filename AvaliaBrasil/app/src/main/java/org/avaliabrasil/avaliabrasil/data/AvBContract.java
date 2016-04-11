package org.avaliabrasil.avaliabrasil.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Pedro on 22/02/2016.
 */
public class AvBContract {
    public final String LOG_TAG = this.getClass().getSimpleName();

    // Content Authority
    public static final String CONTENT_AUTHORITY = "org.avaliabrasil.avaliabrasil";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths Headers
    public static final String PATH_PLACE = "place";

    public static final String PATH_PLACES = "places";

    // Paths Headers
    public static final String PATH_INSTRUMENT = "instruments";

    public static final String PATH_INSTRUMENTS = "instruments/*";

    public static final String PATH_INSTRUMENT_PLACE = "instruments_place";

    public static final String PATH_GROUP = "groups";
    public static final String PATH_GROUPS = "groups/*";

    public static final String PATH_QUESTION = "questions";
    public static final String PATH_QUESTIONS = "questions/*";

    public static final String PATH_SURVEY = "survey";

    public static final String PATH_NEW_PLACE = "newPlace";

    public static final String PATH_PLACE_CATEGORY = "place_category";

    public static final String PATH_PLACE_TYPE = "place_types";

    public static final String PATH_PLACE_TYPES = "place_types/*";

    public static final int INSTRUMENT = 4;
    public static final int INSTRUMENTS = 5;
    public static final int INSTRUMENT_PLACE = 6;
    public static final int GROUP_QUESTION = 7;
    public static final int GROUP_QUESTIONS = 8;
    public static final int QUESTION = 9;
    public static final int QUESTIONS = 10;
    public static final int SURVEY = 11;
    public static final int NEWPLACE = 12;
    public static final int PLACE_CATEGORY = 13;
    public static final int PLACE_TYPE = 14;
    public static final int PLACE_TYPES = 15;


    // Normalizando as datas :

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Definindo a Tabela Place
       Cada classe tem strings que definem a tabela e seus campos
        */
    public static final class PlaceEntry implements BaseColumns {
        public static final Uri PLACE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final Uri PLACES_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;

        // Place Table Name
        public static final String TABLE_NAME = "place";

        // Table Columns
        // Colunas que estarão no Mysql:
        public static final String COLUMN_PLACE_ID = "place_id";
        public static final String COLUMN_NAME = "name";

        // Colunas que não estarão no Mysql, e virão do Google:
        public static final String COLUMN_ADRESS = "adress";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_OPEN_HOURS = "openhours";


//        // Colunas de cadastro necessárias para outras funções, e que incluem
//        "city";
//        "state";
//        "category";
//        "type";
//
//
//        // Colunas com Dados Temporários:
//        "national_rankingPosition";
//        "regional_rankingPosition";
//        "state_rankingPosition";
//        "municipal_rankingPosition";
//
//
//        "national_rankingStatus";
//        "regional_rankingStatus";
//        "state_rankingStatus";
//        "municipal_rankingStatus";
//
//        "lastWeekSurveys";

        // Tabela de Histórico de Índice de Qualidade, para mostrar

        //

        // Construindo a URI do Place
        public static Uri buildGooglePlaceUri (String googlePlaceId) {
            return PLACE_URI.buildUpon().appendPath(googlePlaceId).build();
        }

        // Função para pegar a o Id do Place da Uri:
        public static String getStringFromUri (Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class InstrumentEntry implements BaseColumns {

        public static final Uri INSTRUMENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INSTRUMENT).build();

        public static final Uri INSTRUMENTS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INSTRUMENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENTS;

        // Place Table Name
        public static final String TABLE_NAME = "instrument";

        // Table Columns
        public static final String INSTRUMENT_ID = "instrument_id";

        public static final String UPDATED_AT = "updated_at";

        public static Uri buildInstrumentUri (String place_id) {
            return INSTRUMENT_URI.buildUpon().appendPath(place_id).build();
        }

        // Função para pegar a o Id do Place da Uri:
        public static String getStringFromUri (Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class InstrumentPlaceEntry implements BaseColumns {

        // Place Table Name
        public static final String TABLE_NAME = "instrument_places";

        // Table Columns
        public static final String INSTRUMENT_ID = "instrument_id";

        public static final String PLACE_ID = "place_id";

        public static final Uri INSTRUMENTPLACE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INSTRUMENT_PLACE).build();

    }

    public static final class GroupQuestionEntry implements BaseColumns {

        public static final Uri GROUP_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP).build();

        public static final Uri GROUPS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUPS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENTS;

        // Place Table Name
        public static final String TABLE_NAME = "group_question";

        // Table Columns
        public static final String INSTRUMENT_ID = "instrument_id";

        public static final String GROUP_ID = "group_id";

        public static final String ORDER_QUESTION = "order_question";


        public static Uri buildGroupQuestionsUri (String instrument_id) {
            return GROUP_URI.buildUpon().appendPath(instrument_id).build();
        }
    }

    public static final class QuestionEntry implements BaseColumns {

        public static final Uri QUESTION_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTION).build();

        public static final Uri QUESTIONS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENTS;

        // Place Table Name
        public static final String TABLE_NAME = "question";

        // Table Columns
        public static final String QUESTION_ID = "question_id";

        public static final String QUESTION = "question";

        public static final String QUESTION_TYPE = "questionType";

        public static final String GROUP_ID = "group_id";


        public static Uri buildQuestionsUri (String instrument_id) {
            return QUESTION_URI.buildUpon().appendPath(instrument_id).build();
        }
    }

    public static final class SurveyEntry implements BaseColumns {

        public static final Uri SURVEY_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SURVEY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENT;

        // Place Table Name
        public static final String TABLE_NAME = "survey";

        // Table Columns
        public static final String INSTRUMENT_ID = "instrument_id";

        public static final String GROUP_ID = "group_id";

        public static final String QUESTION_ID = "question_id";

        public static final String QUESTION_TYPE = "question_type";

        public static final String PLACE_ID = "place_id";

        public static final String ANWSER = "anwser";

        public static final String SURVEY_FINISHED = "survey_finished";

    }

    public static final class NewPlaceEntry implements BaseColumns {

        public static final Uri NEWPLACE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEW_PLACE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INSTRUMENT;

        // Place Table Name
        public static final String TABLE_NAME = "newPlace";

        public static final String PLACE_ID = "place_id";

        public static final String CATEGORY_ID = "category_id";

        public static final String PLACE_TYPE_ID = "place_type_id";

    }

    public static final class PlaceCategoryEntry implements BaseColumns {

        public static final Uri PLACE_CATEGORY_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE_CATEGORY;


        // Place Table Name
        public static final String TABLE_NAME = "place_category";

        // Table Columns
        public static final String CATEGORY_ID = "category_id";

        public static final String NAME = "name";
    }

    public static final class PlaceTypeEntry implements BaseColumns {

        public static final Uri PLACE_TYPE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE_TYPE).build();

        public static final Uri PLACE_TYPES_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE_TYPES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE_TYPE;


        // Place Table Name
        public static final String TABLE_NAME = "place_type";

        // Table Columns
        public static final String CATEGORY_ID = "category_id";

        public static final String NAME = "name";

        public static Uri buildPlaceTypeUri (String category_id) {
            return PLACE_TYPE_URI.buildUpon().appendPath(category_id).build();
        }
    }
}
