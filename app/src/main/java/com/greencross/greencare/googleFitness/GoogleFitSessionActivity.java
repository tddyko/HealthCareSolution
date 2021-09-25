/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greencross.greencare.googleFitness;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.greencross.greencare.util.Logger;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;


/**
 * This sample demonstrates how to use the Sessions API of the Google Fit platform to insert
 * sessions into the History API, query against existing data, and remove sessions. It also
 * demonstrates how to authenticate a user with Google Play Services and how to properly
 * represent data in a Session, as well as how to use ActivitySegments.
 */
public class GoogleFitSessionActivity extends AppCompatActivity {
    public static final String TAG = "BasicSessions";
    public static final String SAMPLE_SESSION_NAME = "Afternoon run";
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private GoogleApiClient mClient = null;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();
    }

    /**
     * Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     * to connect to Fitness APIs. The scopes included should match the scopes your app needs
     * (see documentation for details). Authentication will occasionally fail intentionally,
     * and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     * can address. Examples of this include the user never having signed in before, or having
     * multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.SESSIONS_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(
                            new ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Logger.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.  What to do?
                                    // Play with some sessions!!
                                    new InsertAndVerifySessionTask().execute();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Logger.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Logger.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Logger.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                        }
                    })
                    .build();
        }
    }

    /**
     * Create and execute a {@link SessionInsertRequest} to insert a session into the History API,
     * and then create and execute a {@link SessionReadRequest} to verify the insertion succeeded.
     * By using an AsyncTask to make our calls, we can schedule synchronous calls, so that we can
     * query for sessions after confirming that our insert was successful. Using asynchronous calls
     * and callbacks would not guarantee that the insertion had concluded before the read request
     * was made. An example of an asynchronous call using a callback can be found in the example
     * on deleting sessions below.
     */
    private class InsertAndVerifySessionTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //First, create a new session and an insertion request.
            SessionInsertRequest insertRequest = insertFitnessSession();

            // [START insert_session]
            // Then, invoke the Sessions API to insert the session and await the result,
            // which is possible here because of the AsyncTask. Always include a timeout when
            // calling await() to avoid hanging that can occur from the service being shutdown
            // because of low memory or other conditions.
            Logger.i(TAG, "Inserting the session in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.SessionsApi.insertSession(mClient, insertRequest)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the session, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Logger.i(TAG, "There was a problem inserting the session: " +
                        insertStatus.getStatusMessage());
                return null;
            }

            // At this point, the session has been inserted and can be read.
            Logger.i(TAG, "Session insert was successful!");
            // [END insert_session]

            // Begin by creating the query.
            SessionReadRequest readRequest = readFitnessSession();

            // [START read_session]
            // Invoke the Sessions API to fetch the session with the query and wait for the result
            // of the read request. Note: Fitness.SessionsApi.readSession() requires the
            // ACCESS_FINE_LOCATION permission.
            SessionReadResult sessionReadResult =
                    Fitness.SessionsApi.readSession(mClient, readRequest)
                            .await(1, TimeUnit.MINUTES);

            // Get a list of the sessions that match the criteria to check the result.
            Logger.i(TAG, "Session read was successful. Number of returned sessions is: "
                    + sessionReadResult.getSessions().size());
            for (Session session : sessionReadResult.getSessions()) {
                // Process the session
                dumpSession(session);

                // Process the data sets for this session
                List<DataSet> dataSets = sessionReadResult.getDataSet(session);
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
            // [END read_session]

            return null;
        }
    }

    /**
     * Create a {@link SessionInsertRequest} for a run that consists of 10 minutes running,
     * 10 minutes walking, and 10 minutes of running. The request contains two {@link DataSet}s:
     * speed data and activity segments data.
     * <p>
     * {@link Session}s are time intervals that are associated with all Fit data that falls into
     * that time interval. This data can be inserted when inserting a session or independently,
     * without affecting the association between that data and the session. Future queries for
     * that session will return all data relevant to the time interval created by the session.
     * <p>
     * Sessions may contain {@link DataSet}s, which are comprised of {@link DataPoint}s and a
     * {@link DataSource}.
     * A {@link DataPoint} is associated with a Fit {@link DataType}, which may be
     * derived from the {@link DataSource}, as well as a time interval, and a value. A given
     * {@link DataSet} may only contain data for a single data type, but a {@link Session} can
     * contain multiple {@link DataSet}s.
     */
    private SessionInsertRequest insertFitnessSession() {
        Logger.i(TAG, "Creating a new session for an afternoon run");
        // Setting start and end times for our run.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        // Set a range of the run, using a start time of 30 minutes before this moment,
        // with a 10-minute walk in the middle.
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -10);
        long endWalkTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -10);
        long startWalkTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -10);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource speedDataSource = new DataSource.Builder()
                .setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_SPEED)
                .setName(SAMPLE_SESSION_NAME + "- speed")
                .setType(DataSource.TYPE_RAW)
                .build();

        float runSpeedMps = 10;
        float walkSpeedMps = 3;
        // Create a data set of the run speeds to include in the session.
        DataSet speedDataSet = DataSet.create(speedDataSource);

        DataPoint firstRunSpeed = speedDataSet.createDataPoint()
                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
        firstRunSpeed.getValue(Field.FIELD_SPEED).setFloat(runSpeedMps);
        speedDataSet.add(firstRunSpeed);

        DataPoint walkSpeed = speedDataSet.createDataPoint()
                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
        walkSpeed.getValue(Field.FIELD_SPEED).setFloat(walkSpeedMps);
        speedDataSet.add(walkSpeed);

        DataPoint secondRunSpeed = speedDataSet.createDataPoint()
                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
        secondRunSpeed.getValue(Field.FIELD_SPEED).setFloat(runSpeedMps);
        speedDataSet.add(secondRunSpeed);

        // [START build_insert_session_request_with_activity_segments]
        // Create a second DataSet of ActivitySegments to indicate the runner took a 10-minute walk
        // in the middle of the run.
        DataSource activitySegmentDataSource = new DataSource.Builder()
                .setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setName(SAMPLE_SESSION_NAME + "-activity segments")
                .setType(DataSource.TYPE_RAW)
                .build();
        DataSet activitySegments = DataSet.create(activitySegmentDataSource);

        DataPoint firstRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
        firstRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        activitySegments.add(firstRunningDp);

        DataPoint walkingDp = activitySegments.createDataPoint()
                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
        walkingDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
        activitySegments.add(walkingDp);

        DataPoint secondRunningDp = activitySegments.createDataPoint()
                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
        secondRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
        activitySegments.add(secondRunningDp);

        // [START build_insert_session_request]
        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName(SAMPLE_SESSION_NAME)
                .setDescription("Long run around Shoreline Park")
                .setIdentifier("UniqueIdentifierHere")
                .setActivity(FitnessActivities.RUNNING)
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(speedDataSet)
                .addDataSet(activitySegments)
                .build();
        // [END build_insert_session_request]
        // [END build_insert_session_request_with_activity_segments]

        return insertRequest;
    }

    /**
     * Return a {@link SessionReadRequest} for all speed data in the past week.
     */
    private SessionReadRequest readFitnessSession() {
        Logger.i(TAG, "Reading History API results for session: " + SAMPLE_SESSION_NAME);
        // [START build_read_session_request]
        // Set a start and end time for our query, using a start time of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_SPEED)
                .setSessionName(SAMPLE_SESSION_NAME)
                .build();
        // [END build_read_session_request]

        return readRequest;
    }

    private void dumpDataSet(DataSet dataSet) {
        Logger.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            DateFormat dateFormat = getTimeInstance();
            Logger.i(TAG, "Data point:");
            Logger.i(TAG, "\tType: " + dp.getDataType().getName());
            Logger.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Logger.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Logger.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    private void dumpSession(Session session) {
        DateFormat dateFormat = getTimeInstance();
        Logger.i(TAG, "Data returned for Session: " + session.getName()
                + "\n\tDescription: " + session.getDescription()
                + "\n\tStart: " + dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS))
                + "\n\tEnd: " + dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS)));
    }

    /**
     * Delete the {@link DataSet} we inserted with our {@link Session} from the History API.
     * In this example, we delete all step count data for the past 24 hours. Note that this
     * deletion uses the History API, and not the Sessions API, since sessions are truly just time
     * intervals over a set of data, and the data is what we are interested in removing.
     */
    private void deleteSession() {
        Logger.i(TAG, "Deleting today's session data for speed");

        // Set a start and end time for our data, using a start time of 1 day before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        // Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_SPEED)
                .deleteAllSessions() // Or specify a particular session here
                .build();

        // Invoke the History API with the Google API client object and the delete request and
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Logger.i(TAG, "Successfully deleted today's sessions");
                        } else {
                            // The deletion will fail if the requesting app tries to delete data
                            // that it did not insert.
                            Logger.i(TAG, "Failed to delete today's sessions");
                        }
                    }
                });
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
}
