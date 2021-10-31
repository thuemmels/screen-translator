# screen-translator
HTW Berlin IMI Bachelor thesis 

This is a Screen Translator app developed during the course of my bachelor thesis.

Please follow these steps to use the application. This process only needs to be done once:

Preparations
1. Download the release version. 
2. Install Google Cloud SDK (https://cloud.google.com/sdk/docs/install) - please check the prerequisites as well.
3. Create a Google account, if not already existing.

Creating your authentication

4. Start the Google Cloud SDK Shell and run the following command 
    (remove the "/" and insert billing id for <BILLING_PROJECT> - contact me for the billing id)
```
gcloud auth application-default login /
--billing-project=<BILLING_PROJECT> /
--scopes=openid,https://www.googleapis.com/auth/userinfo.email,https://www.googleapis.com/auth/cloud-platform,https://www.googleapis.com/auth/cloud-vision
```

5. A browser window should open, prompting you to log in with your Google account in order to authenticate yourself. 
6. If everything worked, you can close the browser as well as the Shell and start the executable jar. 
