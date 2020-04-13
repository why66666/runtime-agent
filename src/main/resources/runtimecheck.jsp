<%@ page import="com.weaver.check.agent.runtime.RuntimeCheck" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
    System.out.println("runtimecheck.jsp");
    out.println("RuntimeCheck状态："+RuntimeCheck.isStart());
    if(request.getParameter("check").equals("1")){
        if(!RuntimeCheck.isStart()){
            RuntimeCheck.startRumtime();
            if(RuntimeCheck.isStart()){
                out.println("RuntimeCheck Start!已开启");
                System.out.println("--------------runtimecheck-------start------------------");
            }
        }
    }else if (request.getParameter("check").equals("0")){
        if(RuntimeCheck.isStart()) {
            RuntimeCheck.stopRumtime();
            if (!RuntimeCheck.isStart()) {
                out.println("RuntimeCheck Stop!已停止");
                System.out.println("--------------runtimecheck-------stop------------------");
            }
        }
    }
    if(request.getParameter("writelog").equals("1")){
        if(!RuntimeCheck.isStart()){
            RuntimeCheck.startRumtime();
            if(RuntimeCheck.isStart()){
                out.println("RuntimeCheck Writelog Start!已开启");
                System.out.println("--------------runtimecheck-------start------------------");
            }
        }
    }else if (request.getParameter("writelog").equals("0")){
        if(RuntimeCheck.isStart()) {
            RuntimeCheck.stopRumtime();
            if (!RuntimeCheck.isStart()) {
                out.println("RuntimeCheck Writelog Stop!已停止");
                System.out.println("--------------runtimecheck-------stop------------------");
            }
        }
    }
%>