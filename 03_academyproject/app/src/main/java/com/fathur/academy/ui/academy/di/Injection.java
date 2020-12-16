package com.fathur.academy.ui.academy.di;

import android.content.Context;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.LocalDataSource;
import com.fathur.academy.data.source.local.room.AcademyDatabase;
import com.fathur.academy.data.source.remote.RemoteDataSource;
import com.fathur.academy.utils.AppExecutors;
import com.fathur.academy.utils.JsonHelper;

public class Injection {
    public static AcademyRepository provideRepository(Context context) {

        AcademyDatabase database = AcademyDatabase.getInstance(context);

        RemoteDataSource remoteDataSource = RemoteDataSource.getInstance(new JsonHelper(context));
        LocalDataSource localDataSource = LocalDataSource.getInstance(database.academyDao());
        AppExecutors appExecutors = new AppExecutors();

        return AcademyRepository.getInstance(remoteDataSource, localDataSource, appExecutors);
    }
}
