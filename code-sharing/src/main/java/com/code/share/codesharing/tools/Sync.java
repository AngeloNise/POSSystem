package com.code.share.codesharing.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sync {

    @FunctionalInterface
    public interface TestOne{
    void run(String tryone);
    }
    @FunctionalInterface
    public interface CheckedRunnable{
        void run() throws Throwable;
    }
    @FunctionalInterface
    public interface CheckedRunnable2{
        Object run() throws Throwable;
    }

    public void GetTest(TestOne testOne){

        String test = "Hello";

        testOne.run(test);
    }
    public  Map GetFeedback(CheckedRunnable... checkedRunnable){

        Map feedBack = new HashMap();

        try{
            //checkedRunnable.run();
            for(CheckedRunnable chk : checkedRunnable){
                chk.run();
            }
            feedBack.put("status", 200);
        }catch (Throwable ex){
            feedBack.put("status", 500);
            feedBack.put("errMsg", ex.getMessage());
        }

        return feedBack;
    }

    public  Map GetFeedback(CheckedRunnable2... checkedRunnable){

        Map feedBack = new HashMap();

        List<Object> objectList = new ArrayList<>();

        try{
            //checkedRunnable.run();
            for(CheckedRunnable2 chk : checkedRunnable){
                objectList.add( chk.run());
            }
            feedBack.put("status", 200);

            feedBack.put("result", objectList);
        }catch (Throwable ex){
            feedBack.put("status", 500);
            feedBack.put("errMsg", ex.getMessage());
        }

        return feedBack;
    }
}
