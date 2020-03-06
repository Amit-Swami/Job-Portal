package com.example.jobscandidate.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.jobscandidate.Model.Savedjob;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME="JobDB.db";
    private static final int DB_VER=2;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public void addToSavedjob(Savedjob job)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Savedjob(JobId,SavedTitle,SavedCompanyName,SavedCompanyImage,SavedExperience,SavedLocation,SavedSkills,SavedVacancies,SavedWalkinTnV,SavedSalary,SavedPostDate, SavedJobDescription,SavedIndustryType,SavedFunctionalArea,SavedJobRole,SavedEmploymentType,SavedDesiredProfile,SavedCompanyWebsite,SavedCompanyDescription,SavedHrName,SavedHrContact,SavedCategoryId,UserPhone) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                job.getJobId(),
                job.getSavedTitle(),
                job.getSavedCompanyName(),
                job.getSavedCompanyImage(),
                job.getSavedExperience(),
                job.getSavedLocation(),
                job.getSavedSkills(),
                job.getSavedVacancies(),
                job.getSavedWalkinTnV(),
                job.getSavedSalary(),
                job.getSavedPostDate(),
                job.getSavedJobDescription(),
                job.getSavedIndustryType(),
                job.getSavedFunctionalArea(),
                job.getSavedJobRole(),
                job.getSavedEmploymentType(),
                job.getSavedDesiredProfile(),
                job.getSavedCompanyWebsite(),
                job.getSavedCompanyDescription(),
                job.getSavedHrName(),
                job.getSavedHrContact(),
                job.getSavedCategoryId(),
                job.getUserPhone()
        );
        db.execSQL(query);
    }

    public void removeFromSavedjob(String jobId,String userPhone)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM Savedjob WHERE JobId='%s' and UserPhone='%s';",jobId,userPhone);
        db.execSQL(query);
    }
    public boolean isSavedjob(String jobId,String userPhone)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM Savedjob WHERE JobId='%s' and UserPhone='%s';",jobId,userPhone);
        Cursor cursor=db.rawQuery(query,null);
        if (cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Savedjob> getAllSavedJobs(String userPhone)
    {
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        String[] sqlSelect={"UserPhone","JobId","SavedTitle","SavedCompanyName","SavedCompanyImage","SavedExperience",
        "SavedLocation","SavedSkills","SavedVacancies","SavedWalkinTnV","SavedSalary","SavedPostDate","SavedJobDescription","SavedIndustryType","SavedFunctionalArea",
        "SavedJobRole","SavedEmploymentType","SavedDesiredProfile","SavedCompanyWebsite","SavedCompanyDescription","SavedHrName","SavedHrContact","SavedCategoryId"};
        String sqlTable="Savedjob";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Savedjob> result=new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Savedjob(
                        c.getString(c.getColumnIndex("JobId")),
                        c.getString(c.getColumnIndex("SavedTitle")),
                        c.getString(c.getColumnIndex("SavedCompanyName")),
                        c.getString(c.getColumnIndex("SavedCompanyImage")),
                        c.getString(c.getColumnIndex("SavedExperience")),
                        c.getString(c.getColumnIndex("SavedLocation")),
                        c.getString(c.getColumnIndex("SavedSkills")),
                        c.getString(c.getColumnIndex("SavedVacancies")),
                        c.getString(c.getColumnIndex("SavedWalkinTnV")),
                        c.getString(c.getColumnIndex("SavedSalary")),
                        c.getString(c.getColumnIndex("SavedPostDate")),
                        c.getString(c.getColumnIndex("SavedJobDescription")),
                        c.getString(c.getColumnIndex("SavedIndustryType")),
                        c.getString(c.getColumnIndex("SavedFunctionalArea")),
                        c.getString(c.getColumnIndex("SavedJobRole")),
                        c.getString(c.getColumnIndex("SavedEmploymentType")),
                        c.getString(c.getColumnIndex("SavedDesiredProfile")),
                        c.getString(c.getColumnIndex("SavedCompanyWebsite")),
                        c.getString(c.getColumnIndex("SavedCompanyDescription")),
                        c.getString(c.getColumnIndex("SavedHrName")),
                        c.getString(c.getColumnIndex("SavedHrContact")),
                        c.getString(c.getColumnIndex("SavedCategoryId")),
                        c.getString(c.getColumnIndex("UserPhone"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }
}