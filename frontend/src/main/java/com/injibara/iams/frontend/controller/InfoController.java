package com.injibara.iams.frontend.controller;

public class InfoController {
    private static MainController main;
    public static void setMain(MainController m) { main = m; }

    public void goSignIn() { if (main != null) main.showSignIn(); }
    public void goSignUp() { if (main != null) main.showSignUp(); }
    public void goContact() { if (main != null) main.showContact(); }
    public void goHome() { if (main != null) main.showHome(); }
}
