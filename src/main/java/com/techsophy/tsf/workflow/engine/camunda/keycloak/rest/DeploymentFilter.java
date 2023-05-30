package com.techsophy.tsf.workflow.engine.camunda.keycloak.rest;

import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilter;
import lombok.NoArgsConstructor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;


@NoArgsConstructor
public class DeploymentFilter implements Filter {


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {

            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            if (request.getRequestURI().contains("deployment")) {
                boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                if (isMultipart) {
                    MultiReadHttpServletRequest multiReadHttpServletRequest = new MultiReadHttpServletRequest(request);
                    request = multiReadHttpServletRequest;
                    multiReadHttpServletRequest.getParts();
                    ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(1024 * 1024, null));

                    Map<String, List<FileItem>> fileItemMap = upload.parseParameterMap(multiReadHttpServletRequest);
                    if (fileItemMap.containsKey("tenant-id")) {
                        List<FileItem> items = fileItemMap.get("tenant-id");
                        String tenant = items.get(0).getString();
                        if (!tenant.equals("techsophy-platform")) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid tenant");
                            return;
                        }

                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No tenant id found for the request");
                        return;
                    }
                }
            }

            chain.doFilter(request, response);
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }
    }

    public static class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
        private ByteArrayOutputStream cachedBytes;

        public MultiReadHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (cachedBytes == null)
                cacheInputStream();

            return new CachedServletInputStream();
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        private void cacheInputStream() throws IOException {
            /* Cache the inputstream in order to read it multiple times. For
             * convenience, I use apache.commons IOUtils
             */
            cachedBytes = new ByteArrayOutputStream();
            IOUtils.copy(super.getInputStream(), cachedBytes);
        }

        /* An inputstream which reads the cached request body */
        public class CachedServletInputStream extends ServletInputStream {
            private ByteArrayInputStream input;

            public CachedServletInputStream() {
                /* create a new input stream from the cached request body */
                input = new ByteArrayInputStream(cachedBytes.toByteArray());
            }

            @Override
            public int read() throws IOException {
                return input.read();
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


        }
    }
}
