package com.code.share.codesharing.tools;

public class Utils {
    private static Utils single_instance = null;

    public static Utils xo(){
        if (single_instance == null){
            single_instance = new Utils();
        }
        return single_instance;
    }
    // ===========================================>>>
    public Systemm syst(){
        return new Systemm();
    }
    // ===========================================>>>
    public Convertion conv(){
        return new Convertion();
    }
    // ===========================================>>>
    public Sync sync() {return new Sync();}
    // ===========================================>>>
    // ===========================================>>>
    // ===========================================>>>
    // ===========================================>>>
    // ===========================================>>>
}
