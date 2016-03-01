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
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE;

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
            // Alternativa 1: Adicionando como um parâmetro da query:
            // return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_PLACE_ID, googlePlaceId).build();

            // Alternativa 2: Adicionando como um caminho mesmo:
            return CONTENT_URI.buildUpon().appendPath(googlePlaceId).build();
        }

        // Função para pegar a o Id do Place da Uri:
        public static String getPlaceIdFromUri (Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }



}
