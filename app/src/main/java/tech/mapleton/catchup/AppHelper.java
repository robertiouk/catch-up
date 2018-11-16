package tech.mapleton.catchup;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

/**
 * A utility class to provide general helper methods.
 */
class AppHelper {
    private static final String TAG = "AppHelper";
    /**
     * Amazon Cognito constants.
     */
    private static final String USER_POOL_ID = "us-east-1_5WNXNwLkE";
    private static final String CLIENT_ID = "90urpimg4h86s9qp2ad596r5";
    private static final String CLIENT_SECRET = "1lrj79h9fo3ddleevr39gh8p48oo64io9n5uru69c3t357klqoji";
    private static final Regions COGNITO_REGION = Regions.US_EAST_1;
    private static AppHelper INSTANCE;
    private final CognitoUserPool userPool;

    private AppHelper(final CognitoUserPool userPool) {
        this.userPool = userPool;
    }

    /**
     * Initialise values using a given application context.
     * @param context the given application context
     */
    static void init(final Context context) {
       INSTANCE = new AppHelper(
               new CognitoUserPool(context, USER_POOL_ID, CLIENT_ID, CLIENT_SECRET, COGNITO_REGION));
    }

    /**
     * Get the Cognito User Pool.
     * @return the Cognito User Pool
     */
    static CognitoUserPool getUserPool() {
        return INSTANCE.userPool;
    }

    static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e(TAG, " -- Error: "+exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            return formattedString;
        }

        return  formattedString;
    }
}
