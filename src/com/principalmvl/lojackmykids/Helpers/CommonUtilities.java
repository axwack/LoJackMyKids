/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.principalmvl.lojackmykids.Helpers;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

	 /**
     * Sometimes we may need to put the IP of the machine. Android expects this to run on a server. Localhost doesn't know where it is.
     */
	
	//TODO: Change the IP of the Server for Debugging and Testing
	//public static final String SERVER_IP_NAME = "localhost:8888/";
	public static final String SERVER_IP_NAME = "metal-complex-658.appspot.com/";
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    //public static final String SERVER_URL = "http://"+SERVER_IP_NAME+":8080";
	public static final String SERVER_URL = "http://"+SERVER_IP_NAME;
    /**
     * API Key for Android Apps from the Google API console 
     * https://console.developers.google.com/project/apps~metal-complex-658/apiui/credential
     */
    public static final String AP_KEY= "AIzaSyANlEVQ4kJQoI5lP7uVtgYf0VlGypht-F4";
    
    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "22298386436";
    // PROJECT ID: https://console.developers.google.com/project/apps~metal-complex-658

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
