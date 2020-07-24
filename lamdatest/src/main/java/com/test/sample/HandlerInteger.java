package com.test.sample;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

public class HandlerInteger implements RequestStreamHandler{
 
 
  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
  throws IOException 
  {
    JSONParser parser = new JSONParser();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    JSONObject responseJson = new JSONObject();

    LambdaLogger logger = context.getLogger();
    int value = 0;
    try {
        JSONObject event = (JSONObject) parser.parse(reader);
        JSONObject responseBody = new JSONObject();
 
        if (event.get("pathParameters") != null) {
            JSONObject pps = (JSONObject) event.get("pathParameters");
            if (pps.get("sampleValue") != null) {
              value = Integer.parseInt((String) pps.get("sampleValue"));
            }
        } else if (event.get("queryStringParameters") != null) {
            JSONObject qps = (JSONObject) event.get("queryStringParameters");
            if (qps.get("sampleValue") != null) {
              value = Integer.parseInt((String) qps.get("sampleValue"));
            }
        }
        if (value > 0) {
            logger.log("value: " + value +"\n");
            responseBody.put("value", value+10);
            responseJson.put("statusCode", 200);
        } else {
            logger.log("Error less than zero \n");
            responseBody.put("ErrorMessage", "Less than zero");
            responseJson.put("statusCode", 404);
        }
 
        JSONObject headerJson = new JSONObject();
        responseJson.put("body", responseBody.toString());
 
    } catch (ParseException pex) {
        responseJson.put("statusCode", 400);
        responseJson.put("exception", pex);
    }
 
    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toString());
    writer.close();
  }
}