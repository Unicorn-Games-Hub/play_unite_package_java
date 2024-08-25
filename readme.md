# Subscription Status Checker

This project demonstrates how to check a user's subscription status using their Google Advertising ID (GAID). The app sends the GAID to a server, which returns a JSON response indicating whether the subscription is active.

## Prerequisites

- Android Studio
- Basic knowledge of Android development (Java)

## Setup Instructions

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/SubscriptionChecker.git
   cd SubscriptionChecker
   Open the Project in Android Studio
   ```

Open the project directory in Android Studio.
Add Internet Permission

Add the following line to your AndroidManifest.xml to allow the app to access the internet:
Copy code

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Include Dependencies

Add the following dependency to your build.gradle file:
Copy code

```gradle
dependencies {
implementation 'com.google.android.gms:play-services-ads-identifier:18.0.1'
}
```

## Checking Subscription Status

- The app fetches the GAID using Google's Advertising ID service.
- It sends the GAID to the server endpoint: https://play-unite.com/checkSubscription?deviceIdentifier=<GAID>.
- The server returns a JSON response, which the app parses to check if the subscription is active.
  Usage
- Upon launching the app, it automatically checks the subscription status.
- Logs will indicate whether the subscription is active or not.
