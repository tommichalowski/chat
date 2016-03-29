package com.gft.bench.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gft.bench.ResultType;
import com.gft.bench.ResultMsg;

public class CmdLineTool {

    private static final Log log = LogFactory.getLog(CmdLineTool.class);
    BufferedReader reader = null;

    public CmdLineTool() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public ResultMsg readLine() {
        ResultMsg msg = new ResultMsg();
        try {
            String str = reader.readLine();
            if ("exit".equalsIgnoreCase(str)) {
                msg.setResult(ResultType.EXIT);
            } else {
                msg.setResult(ResultType.NORMAL);
                msg.setMessage(str);
            }
        } catch (IOException e) {
            msg.setResult(ResultType.ERROR);
            log.error(e.getStackTrace());
        }
        return msg;
    }
}
