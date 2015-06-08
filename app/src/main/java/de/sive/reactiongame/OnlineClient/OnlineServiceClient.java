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

/**
 * Created by Michael on 19.05.2015.
 */
public class OnlineServiceClient {

    //Constants
    public static final String NAMESPACE = "http://ServiceInterface.reactiongame.sive.de/";
    public static final String URL = "http://10.0.2.2:8080/reactiongame/ReactiongameServiceBeanService";
    private static String TAG = OnlineServiceClient.class.getName();

    //Class variables
    private int sessionId;

    public GameSmallestNumber createGame(String[] answers) throws NoGameException {
        Log.d(TAG, "createGame was called...");
        String METHOD_NAME = "createGame";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME);
            Log.d(TAG, response.toString());
            /*//Get answers
            String[] answers = new String[4];
            for(int i = 0; i< 3; i++)
            {
                answers[i] = response.getPropertyAsString(i);
            }*/
            //Get game duration
            int gameDuration = Integer.parseInt(response.getProperty("gameDurationInMilliseconds").toString());
            //Get the index of the correct answer
            int correctAnswerIndex = Integer.parseInt(response.getProperty("gameDurationInMilliseconds").toString());
            //Return the recieved smallest number game
            return new GameSmallestNumber(answers, correctAnswerIndex, gameDuration);

        } catch (SoapFault e) {
            throw new NoGameException(e.getMessage());
        } catch (Exception e) {
            throw new NoGameException(e.getMessage());
        }

    }

    public String setGameResult(int playerNumber, boolean isWinner) throws NoGameException {
        Log.d(TAG, "setGameResult was called...");
        String METHOD_NAME = "setGameResult";
        try {
            SoapObject response = executeSoapAction(METHOD_NAME, playerNumber, isWinner);
            Log.d(TAG, response.toString());

            //Return the response message
            return response.getProperty("message").toString();

        } catch (SoapFault e) {
            throw new NoGameException(e.getMessage());
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
