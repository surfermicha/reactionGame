package de.sive.reactiongame.onlineClient;

import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;

import GameServer.GameSmallestNumber;
import GameServer.SessionData;

/**
 * This is the Stub to communicate with the Wildfly server via SOAP.
 * Note: This methods are time intensive. Pay Attention that you call them async.
 * Note: In development mode change the IP in in the URL constant to the IP of the wildfly server in your local network
 * @author Michael Landreh
 */
public class OnlineServiceClient {

    //Constants
    public static final String NAMESPACE = "http://ServiceInterface.reactiongame.sive.de/";
    public static final String URL = "http://192.168.0.15:8080/reactiongame/ReactiongameServiceBean";
    private static final int LOGIN_FAILED_PASSWORD_CODE = 40;
    private static final int LOGIN_FAILED_EMAIL_CODE = 41;
    private static final int LOGIN_FAILED_ERROR_CODE = 80;
    private static final int OK_CODE = 0;
    private static String TAG = OnlineServiceClient.class.getName(); //Log-Tag

    //Class variables
    private int sessionId;

    /*
    This method requests a GameSmallestNumber from the server
    @return GameSmallestNumber The recieved game from server
     */
    public GameSmallestNumber createGame() throws NoGameException {
        Log.d(TAG, "createGame was called...");
        String METHOD_NAME = "createGame";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME);
            Log.d(TAG, response.toString());
            //Get answers
            String[] answers = new String[4];
            for(int i = 0; i<=3; i++)
            {
                answers[i] = response.getPropertyAsString(i+2);
            }
            //Get game duration
            int gameDuration = Integer.parseInt(response.getProperty("gameDurationInMilliseconds").toString());
            //Get the index of the correct answer
            int correctAnswerIndex = Integer.parseInt(response.getProperty("correctAnswerIndex").toString());
            //Return the recieved smallest number game
            return new GameSmallestNumber(answers, correctAnswerIndex, gameDuration);

        } catch (SoapFault e) {
            throw new NoGameException(e.getMessage());
        } catch (Exception e) {
            throw new NoGameException(e.getMessage());
        }

    }

    /*
     * This method pushes the result of GameSmallestNumber to the server
     * @param   int playerNumber
     */
    public int setGameResult(int playerNumber, int selectedAnswerIndex, boolean isWinner) throws ResultNotPushedException {
        Log.d(TAG, "setGameResult was called...");
        String METHOD_NAME = "setGameResult";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME, playerNumber, selectedAnswerIndex, isWinner);
            Log.d(TAG, response.toString());

            //Return the response message
            return Integer.parseInt(response.getProperty("returnCode").toString());

        } catch (SoapFault e) {
            throw new ResultNotPushedException(e.getMessage());
        }
    }

    /**
     * This method sends the user credentials to the server and receives a token to access the server
     * @param email The users e-mail-address
     * @param password The users password
     */
    public SessionData loginUser(String email, String password) throws InvalidLoginException {
        Log.d(TAG, "loginUser was called...");
        String METHOD_NAME = "loginUser";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME, email, password);
            Log.d(TAG, response.toString());

            switch (Integer.parseInt(response.getProperty("returnCode").toString())) {
                case LOGIN_FAILED_EMAIL_CODE:
                    throw new InvalidLoginException("Login failed caused by wrong e-mail", LOGIN_FAILED_EMAIL_CODE);
                case LOGIN_FAILED_PASSWORD_CODE:
                    throw new InvalidLoginException("Login failed caused by wrong password", LOGIN_FAILED_PASSWORD_CODE);
                default:
                    break;
            }

            String mEmail = response.getPropertyAsString("email");
            String mFirstname = response.getPropertyAsString("firstname");
            String mLastname = response.getPropertyAsString("lastname");
            int mSessionId = Integer.parseInt(response.getPropertyAsString("sessionId"));
            return new SessionData(mSessionId, mEmail, mFirstname, mLastname);
        } catch (SoapFault e) {
            throw new InvalidLoginException(e.getMessage(),LOGIN_FAILED_ERROR_CODE);
        } catch (InvalidLoginException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidLoginException("Technical error: " + e.getMessage(), LOGIN_FAILED_ERROR_CODE);
        }
    }


    public SessionData createUser(String email, String password, String firstname, String lastname) throws UserAlreadyExistsException {
        Log.d(TAG, "createUser was called...");
        String METHOD_NAME = "createUser";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME, email, password, firstname, lastname);
            Log.d(TAG, response.toString());

            //Check whether the response is null
            if (Integer.parseInt(response.getProperty("returnCode").toString()) != OK_CODE) {
                throw new UserAlreadyExistsException("The user already exists");
            }

            String mEmail = response.getPropertyAsString("email");
            String mFirstname = response.getPropertyAsString("firstname");
            String mLastname = response.getPropertyAsString("lastname");
            int mSessionId = Integer.parseInt(response.getPropertyAsString("sessionId"));
            return new SessionData(mSessionId, mEmail, mFirstname, mLastname);
        } catch (SoapFault e) {
            Log.w(TAG, "SoapFault: " + e.getMessage());

        }
        return null;
    }

    /**
     * Use this method to log out the current user
     * @param sessionId The stored sessionid.
     * @return
     */

    /**
     * This method returns true or false depending on whether the given sessionId is available or not
     * @return boolean Returns true if the given session ID is available
     */
    public boolean isSessionAvailable(int sessionId) throws Exception{
        Log.d(TAG, "isSessionAvailable was called...");
        String METHOD_NAME = "isSessionAvailable";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME, sessionId);
            Log.d(TAG, response.toString());

            //Check whether the response is null
            if (Integer.parseInt(response.getProperty("returnCode").toString()) != OK_CODE) {
                throw new Exception("Couldn't find server session status.");
            }
            return Boolean.parseBoolean(response.getPropertyAsString("sessionValid"));
        } catch (SoapFault e) {
            Log.w(TAG, "SoapFault: " + e.getMessage());
            return false;
        }
    }

    private SoapObject executeSoapAction(String methodName, Object... args) throws SoapFault {

        Object result = null;

	    /* Create a org.ksoap2.serialization.SoapObject object to build a SOAP request. Specify the namespace of the SOAP object and method
         * name to be invoked in the SoapObject constructor.
	     */
        SoapObject request = new SoapObject(NAMESPACE, methodName);

	    /* The array of arguments is copied into properties of the SOAP request using the addProperty method. */
        for (int i = 0; i < args.length; i++) {
            request.addProperty("arg" + i, args[i]);
        }

	    /* Next create a SOAP envelop. Use the SoapSerializationEnvelope class, which extends the SoapEnvelop class, with support for SOAP
         * Serialization format, which represents the structure of a SOAP serialized message. The main advantage of SOAP serialization is portability.
	     * The constant SoapEnvelope.VER11 indicates SOAP Version 1.1, which is default for a JAX-WS webservice endpoint under JBoss.
	     */
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

	    /* Assign the SoapObject request object to the envelop as the outbound message for the SOAP method call. */
        envelope.setOutputSoapObject(request);

	    /* Create a org.ksoap2.transport.HttpTransportSE object that represents a J2SE based HttpTransport layer. HttpTransportSE extends
	     * the org.ksoap2.transport.Transport class, which encapsulates the serialization and deserialization of SOAP messages.
	     */
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);


        try {
	        /* Make the soap call using the SOAP_ACTION and the soap envelop. */
            List<HeaderProperty> reqHeaders = null;

            @SuppressWarnings({"unused", "unchecked"})
            //List<HeaderProperty> respHeaders = androidHttpTransport.call(NAMESPACE + methodName, envelope, reqHeaders);
                    //fuehrt zu CXF-Fehler! neue Version ohne SOAP-Action funktioniert:
                    List<HeaderProperty> respHeaders = androidHttpTransport.call("", envelope, reqHeaders);

	        /* Get the web service response using the getResponse method of the SoapSerializationEnvelope object.
	         * The result has to be cast to SoapPrimitive, the class used to encapsulate primitive types, or to SoapObject.
	         */
            result = envelope.getResponse();

            if (result instanceof SoapFault) {
                throw (SoapFault) result;
            }
        } catch (SoapFault e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (SoapObject) result;
    }


}
