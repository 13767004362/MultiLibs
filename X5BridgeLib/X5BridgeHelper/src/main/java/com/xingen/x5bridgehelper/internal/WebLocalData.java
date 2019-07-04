package com.xingen.x5bridgehelper.internal;

import java.util.List;

/**
 * @author HeXinGen
 * date 2019/1/31.
 */
public class WebLocalData {
    private String dir;
    private List<String> localResourceList;
    private WebLocalData (){

    }
    public static WebLocalData create(){
        WebLocalData data=new WebLocalData();
        return data;
    }

    public WebLocalData setDir(String dir) {
        this.dir = dir;
        return this;
    }
    public WebLocalData setLocalResourceList(List<String> localResourceList) {
        this.localResourceList = localResourceList;
        return this;
    }

    public String getDir() {
        return dir;
    }

    public List<String> getLocalResourceList() {
        return localResourceList;
    }

}
