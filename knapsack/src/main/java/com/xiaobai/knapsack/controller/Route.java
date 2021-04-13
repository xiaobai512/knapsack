package com.xiaobai.knapsack.controller;

import com.xiaobai.knapsack.utils.JDBCUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
public class Route {
    @RequestMapping("/index")
    public String hello() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getIndex");
        return "index";
    }

    @RequestMapping("/getfile")
    public String getFile() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getFile");
        return "getfile";
    }

    @RequestMapping("/getshow")
    public String getShow() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getShow");
        return "getshow";
    }

    @RequestMapping("/getscot")
    public String getScot() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getScot");
        return "getscot";
    }

    @RequestMapping("/getsort")
    public String getSort() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getsort");
        return "getsort";
    }

    @RequestMapping("/getdynamic")
    public String getDynamic() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getDynamic");
        return "getdynamic";
    }

    @RequestMapping("/getbacktrack")
    public String getBacktrack() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getBacktrack");
        return "getbacktrack";
    }

    @RequestMapping("/getgenetic")
    public String getGenetic() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getGenetic");
        return "getgenetic";
    }

    @RequestMapping("/getjournal")
    public String getJournal() throws SQLException {
        JDBCUtils.recordJournal("路由转发","getJournal");
        return "getjournal";
    }
}
