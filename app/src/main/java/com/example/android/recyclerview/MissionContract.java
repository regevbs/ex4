package com.example.android.recyclerview;

import android.provider.BaseColumns;

/**
 * Created by Regev on 4/26/2017.
 */

public class MissionContract {

    public class MissionEntry implements BaseColumns {
        public static final String TABLE_NAME = "missions";
        public static final String COLUMN_MISSION_DESC = "description";
        public static final String COLUMN_DUE_DATE = "dueDate";

    }


}
