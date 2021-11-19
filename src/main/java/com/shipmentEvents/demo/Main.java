package com.main;

public class Main {
  public Main() {
    configureApp();
  }
  
  private void configureApp() {
    GoSellSDK.init(this, "sk_test_kovrMB0mupFJXfNZWx6Etg5y","company.tap.goSellSDKExample");  // to be replaced by merchant
    GoSellSDK.setLocale("en");//  language to be set by merchant
  }
}
