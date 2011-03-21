package jp.sblo.pandora.jota;

import java.lang.reflect.Method;

public class IS01FullScreen {
    static Method setFullScreenMode=null;

    static public void createInstance(){
        try{
            Class<?> sgManager = Class.forName("jp.co.sharp.android.softguide.SoftGuideManager");
            Class<?> paramstype[] = {boolean.class};
            setFullScreenMode = sgManager.getMethod("setFullScreenMode", paramstype);
        }catch(Exception o){
        }
    }

    static public boolean isIS01orLynx()
    {
        return setFullScreenMode!=null;
    }

    static public void setFullScreenOnIS01()
    {
        if ( setFullScreenMode!=null ){
            try{
                setFullScreenMode.invoke(null,true);
            }catch(Exception e){}
        }
    }

}
