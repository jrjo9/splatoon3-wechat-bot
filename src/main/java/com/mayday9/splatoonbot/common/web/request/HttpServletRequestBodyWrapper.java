package com.mayday9.splatoonbot.common.web.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

//body默认只能读取一次，为了可重复读，增加
public class HttpServletRequestBodyWrapper extends HttpServletRequestWrapper {
    private static final Logger logger = LoggerFactory.getLogger(HttpServletRequestBodyWrapper.class);

    private byte[] body;

    public HttpServletRequestBodyWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = readBodyToString(request).getBytes(Charset.forName("UTF-8"));
        request.setAttribute("isBodyWrapped", true);
    }

    public String getBody() {
        return new String(body);
    }

    private String readBodyToString(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = request.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            logger.error(" readBodyToString exception", e);
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        };
    }
}
